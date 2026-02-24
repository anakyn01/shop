package com.hbk.controller;

import com.hbk.dto.MainVideoRequest;
import com.hbk.dto.MainVideoResponse;
import com.hbk.service.MainVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ✅ 메인 비디오 컨트롤러 (영상 + 텍스트 + 버튼2 / 항상 1개 유지)
 *
 * API
 * - GET  /api/main-video
 * - POST /api/main-video   (multipart/form-data) ✅ 항상 1개 유지(기존 DB/파일 삭제 후 저장) / video 필수
 * - PUT  /api/main-video   (multipart/form-data) ✅ 수정(영상 선택이면 교체, 없으면 텍스트/링크만 변경)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main-video")
public class MainVideoController {

    private final MainVideoService service;

    /** ✅ 최신 1개 조회 */
    @GetMapping
    public MainVideoResponse getLatest() {
        return service.getLatest();
    }

    /** ✅ POST: 항상 1개 유지(교체) - video 필수 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MainVideoResponse createReplace(
            @ModelAttribute MainVideoRequest req,
            @RequestParam("video") MultipartFile video
    ) {
        return service.createReplace(req, video);
    }

    /** ✅ PUT: 수정 - video 선택(없으면 텍스트/링크만 수정) */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MainVideoResponse update(
            @ModelAttribute MainVideoRequest req,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) {
        return service.update(req, video);
    }
}