package com.hbk.service;

import com.hbk.dto.MainVideoMapper;
import com.hbk.dto.MainVideoRequest;
import com.hbk.dto.MainVideoResponse;
import com.hbk.entity.MainVideo;
import com.hbk.repository.MainVideoRepository;
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
public class MainVideoService {

    private final MainVideoRepository repository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    /** ✅ 최신 1개 조회 (DTO 반환) */
    @Transactional(readOnly = true)
    public MainVideoResponse getLatest() {
        return MainVideoMapper.toResponse(repository.findTopByOrderByIdDesc().orElse(null));
    }

    /**
     * ✅ POST: 항상 1개 유지
     * - 기존 비디오 있으면 DB 삭제 + 파일 삭제
     * - 새 영상 저장 + DB 저장
     */
    public MainVideoResponse createReplace(MainVideoRequest req, MultipartFile video) {
        if (req == null) throw new IllegalArgumentException("request is required");
        validateRequiredText(req);

        if (video == null || video.isEmpty()) throw new IllegalArgumentException("video is required");

        // 1) 기존 데이터 삭제(파일까지)
        Optional<MainVideo> oldOpt = repository.findTopByOrderByIdDesc();
        oldOpt.ifPresent(this::deleteVideoAndFile);

        // 2) 새 파일 저장
        StoredFile stored = storeVideo(video);

        // 3) 엔티티 저장
        MainVideo entity = MainVideo.builder()
                .videoUrl(stored.publicUrl) // /uploads/main-video/xxx.mp4
                .build();

        // 텍스트 적용
        MainVideoMapper.apply(entity, req);

        return MainVideoMapper.toResponse(repository.save(entity));
    }

    /**
     * ✅ PUT: 수정
     * - video 있으면: 기존 파일 삭제 + 새 파일 저장 + videoUrl 교체
     * - video 없으면: 텍스트/링크만 수정
     */
    public MainVideoResponse update(MainVideoRequest req, MultipartFile video) {
        if (req == null) throw new IllegalArgumentException("request is required");
        validateRequiredText(req);

        MainVideo current = repository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalStateException("no main video. create first."));

        // 텍스트 업데이트
        MainVideoMapper.apply(current, req);

        // 영상 교체(선택)
        if (video != null && !video.isEmpty()) {
            deleteFileByPublicUrl(current.getVideoUrl());
            StoredFile stored = storeVideo(video);
            current.setVideoUrl(stored.publicUrl);
        }

        return MainVideoMapper.toResponse(repository.save(current));
    }

    // -------------------------
    // 내부: 유효성/파일 저장/삭제
    // -------------------------
    private void validateRequiredText(MainVideoRequest req) {
        String title = safeTrim(req.getTitle());
        String subtitle = safeTrim(req.getSubtitle());

        if (title.isEmpty()) throw new IllegalArgumentException("title is required");
        if (subtitle.isEmpty()) throw new IllegalArgumentException("subtitle is required");
    }

    private record StoredFile(String publicUrl, String originalName) {}

    private StoredFile storeVideo(MultipartFile file) {
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename()
        );

        // 확장자(.mp4 등)
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            ext = originalName.substring(dot);
        }

        // 저장 폴더: {uploadDir}/main-video
        Path dir = Paths.get(uploadDir, "main-video").toAbsolutePath().normalize();
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename).normalize();

        // 폴더 탈출 방지
        if (!target.startsWith(dir)) throw new IllegalStateException("invalid file path");

        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("video save failed", e);
        }

        String publicUrl = "/uploads/main-video/" + filename;
        return new StoredFile(publicUrl, originalName);
    }

    private void deleteVideoAndFile(MainVideo v) {
        deleteFileByPublicUrl(v.getVideoUrl());
        repository.delete(v);
    }

    private void deleteFileByPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) return;

        // /uploads/main-video/xxx.mp4 -> filename: xxx.mp4
        String filename = publicUrl.substring(publicUrl.lastIndexOf('/') + 1);
        Path path = Paths.get(uploadDir, "main-video", filename).toAbsolutePath().normalize();

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("video delete failed: " + path + " / " + e.getMessage());
        }
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}