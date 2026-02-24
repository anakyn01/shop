package com.hbk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerCreateRequest {

    private String title;
    private String desc;

    // optional
    private String linkUrl;
    private Integer sortOrder;

    // "Y" | "N"  (프론트와 동일)
    private String visibleYn; // null이면 Service/Controller에서 "Y"로 처리
}