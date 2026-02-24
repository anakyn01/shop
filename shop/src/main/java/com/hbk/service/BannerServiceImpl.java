package com.hbk.service;


import com.hbk.dto.BannerCreateRequest;
import com.hbk.dto.BannerMapper;
import com.hbk.dto.BannerResponse;
import com.hbk.entity.Banner;
import com.hbk.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    // ✅ application.properties의 app.upload-dir 바로 사용
    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> list() {
        return bannerRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(BannerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> listVisible() {
        return bannerRepository.findByVisibleYnOrderBySortOrderAsc("Y")
                .stream()
                .map(BannerMapper::toResponse)
                .toList();
    }

    @Override
    public BannerResponse create(BannerCreateRequest req, MultipartFile image) {
        // 1) 기본 검증
        String title = trim(req.getTitle());
        String desc  = trim(req.getDesc());

        if (title.isEmpty()) throw new IllegalArgumentException("title is required");
        if (desc.isEmpty()) throw new IllegalArgumentException("desc is required");
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("image is required");

        // 2) 파일 저장 (uploads/banners/...)
        StoredFile stored = storeBannerImage(image);

        // 3) 엔티티 저장
        Banner banner = Banner.builder()
                .title(title)
                .desc(desc)
                .linkUrl(emptyToNull(req.getLinkUrl()))
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .visibleYn((req.getVisibleYn() == null || req.getVisibleYn().isBlank()) ? "Y" : req.getVisibleYn())
                // ✅ AppConfig에서 /uploads/** 매핑하므로 이 URL로 프론트 접근 가능
                .imageUrl(stored.publicUrl) // 예: /uploads/banners/uuid.png
                .imageOriginalName(stored.originalName)
                .imageContentType(stored.contentType)
                .imageSize(stored.size)
                .build();

        return BannerMapper.toResponse(bannerRepository.save(banner));
    }

    // -------------------------
    // 내부 로직
    // -------------------------
    private record StoredFile(String publicUrl, String originalName, String contentType, long size) {}

    private StoredFile storeBannerImage(MultipartFile file) {
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename()
        );

        // 확장자(.png/.jpg 등)
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            ext = originalName.substring(dot);
        }

        // 저장 폴더: {uploadDir}/banners
        Path dir = Paths.get(uploadDir, "banners").toAbsolutePath().normalize();
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename).normalize();

        // 폴더 탈출 방지
        if (!target.startsWith(dir)) throw new IllegalStateException("invalid file path");

        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("failed to store image", e);
        }

        String publicUrl = "/uploads/banners/" + filename;

        return new StoredFile(
                publicUrl,
                originalName,
                file.getContentType(),
                file.getSize()
        );
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String emptyToNull(String s) {
        String t = trim(s);
        return t.isEmpty() ? null : t;
    }
}