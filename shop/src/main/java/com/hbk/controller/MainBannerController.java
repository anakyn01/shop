package com.hbk.controller;

import com.hbk.dto.MainBannerResponse;
import com.hbk.service.MainBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ✅ 메인 배너 컨트롤러 (이미지 + 링크만 / 항상 1개 유지)
 *
 * API
 * - GET  /api/main-banner
 * - POST /api/main-banner   (multipart/form-data)  ✅ 항상 1개 유지(기존 DB/파일 삭제 후 저장) / image 필수
 * - PUT  /api/main-banner   (multipart/form-data)  ✅ 수정(이미지 선택이면 교체, 없으면 링크만 변경)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main-banner")
public class MainBannerController {

    private final MainBannerService service;

    /** ✅ 최신 1개 조회 */
    @GetMapping
    public MainBannerResponse getLatest() {
        return service.getLatest();
    }

    /** ✅ POST: 항상 1개 유지 (기존 DB/파일 자동 삭제 후 새로 저장) */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MainBannerResponse createReplace(
            @RequestParam("image") MultipartFile image,
            @RequestParam(required = false) String linkUrl
    ) {
        return service.createReplace(image, linkUrl);
    }

    /** ✅ PUT: 수정 (image 없으면 linkUrl만 변경, image 있으면 교체) */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MainBannerResponse update(
            @RequestParam(required = false) String linkUrl,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        return service.update(linkUrl, image);
    }
}