package com.hbk.controller;

import com.hbk.dto.BannerCreateRequest;
import com.hbk.dto.BannerResponse;
import com.hbk.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banners")
public class BannerController {

    // ✅ 인터페이스로 주입 (BannerServiceImpl이 자동으로 주입됨)
    private final BannerService bannerService;

    /** ✅ 관리자: 전체 배너 목록 */
    @GetMapping
    public List<BannerResponse> list() {
        return bannerService.list();
    }

    /** ✅ 사용자: 노출(Y) 배너 목록 (원하면 프론트에서 여기 호출) */
    @GetMapping("/visible")
    public List<BannerResponse> listVisible() {
        return bannerService.listVisible();
    }

    /** ✅ 배너 생성 (FormData + 이미지) */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BannerResponse create(
            @RequestParam String title,
            @RequestParam String desc,
            @RequestParam(required = false) String linkUrl,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(defaultValue = "Y") String visibleYn,
            @RequestParam("image") MultipartFile image
    ) {
        BannerCreateRequest req = BannerCreateRequest.builder()
                .title(title)
                .desc(desc)
                .linkUrl(linkUrl)
                .sortOrder(sortOrder)
                .visibleYn(visibleYn)
                .build();

        return bannerService.create(req, image);
    }
}