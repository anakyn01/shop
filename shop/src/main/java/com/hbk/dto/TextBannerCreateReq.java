package com.hbk.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextBannerCreateReq {

    private String title;
    private String desc;
    private String linkUrl;
    private Integer sortOrder;
    private String visibleYn; // "Y" | "N" (없으면 서버에서 "Y" 기본 처리 추천)

    // ✅ 프론트 FormData key: "image"
    private MultipartFile image;
}
