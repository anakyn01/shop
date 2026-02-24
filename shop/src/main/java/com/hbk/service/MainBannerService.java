package com.hbk.service;

import com.hbk.dto.MainBannerMapper;
import com.hbk.dto.MainBannerResponse;
import com.hbk.entity.MainBanner;
import com.hbk.repository.MainBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MainBannerService {

    private final MainBannerRepository repository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    /** ✅ 최신 1개 조회 (DTO 반환) */
    @Transactional(readOnly = true)
    public MainBannerResponse getLatest() {
        return MainBannerMapper.toResponse(repository.findTopByOrderByIdDesc().orElse(null));
    }

    /**
     * ✅ POST: 항상 1개 유지
     * - 기존 배너 있으면 DB 삭제 + 파일 삭제
     * - 새 이미지 저장 + DB 저장
     */
    public MainBannerResponse createReplace(MultipartFile image, String linkUrl) {
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("image is required");

        // 1) 기존 배너 삭제(파일까지)
        Optional<MainBanner> oldOpt = repository.findTopByOrderByIdDesc();
        oldOpt.ifPresent(this::deleteBannerAndFile);

        // 2) 새 파일 저장
        StoredFile stored = storeImage(image);

        // 3) DB 저장
        MainBanner banner = MainBanner.builder()
                .imageUrl(stored.publicUrl) // /uploads/main/xxx.png
                .linkUrl(emptyToNull(linkUrl))
                .build();

        return MainBannerMapper.toResponse(repository.save(banner));
    }

    /**
     * ✅ PUT: 수정
     * - image가 있으면: 기존 파일 삭제 + 새 파일 저장 + imageUrl 교체
     * - image가 없으면: linkUrl만 변경
     */
    public MainBannerResponse update(String linkUrl, MultipartFile image) {
        MainBanner current = repository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalStateException("no main banner. create first."));

        // 링크 업데이트 (빈 값이면 null)
        current.setLinkUrl(emptyToNull(linkUrl));

        // 이미지가 있으면 교체
        if (image != null && !image.isEmpty()) {
            deleteFileByPublicUrl(current.getImageUrl());
            StoredFile stored = storeImage(image);
            current.setImageUrl(stored.publicUrl);
        }

        return MainBannerMapper.toResponse(repository.save(current));
    }

    // -------------------------
    // 내부: 파일 저장/삭제
    // -------------------------
    private record StoredFile(String publicUrl, String originalName) {}

    private StoredFile storeImage(MultipartFile file) {
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename()
        );

        // 확장자
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            ext = originalName.substring(dot);
        }

        // 저장 폴더: {uploadDir}/main
        Path dir = Paths.get(uploadDir, "main").toAbsolutePath().normalize();
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename).normalize();

        // 폴더 탈출 방지
        if (!target.startsWith(dir)) throw new IllegalStateException("invalid file path");

        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("file save failed", e);
        }

        String publicUrl = "/uploads/main/" + filename;
        return new StoredFile(publicUrl, originalName);
    }

    private void deleteBannerAndFile(MainBanner banner) {
        deleteFileByPublicUrl(banner.getImageUrl());
        repository.delete(banner);
    }

    private void deleteFileByPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) return;

        // publicUrl: /uploads/main/xxx.png -> filename: xxx.png
        String filename = publicUrl.substring(publicUrl.lastIndexOf('/') + 1);
        Path path = Paths.get(uploadDir, "main", filename).toAbsolutePath().normalize();

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("file delete failed: " + path + " / " + e.getMessage());
        }
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}