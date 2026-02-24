package com.hbk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerResponse {

    private Long id;

    private String title;
    private String desc;
    private String linkUrl;
    private Integer sortOrder;
    private String visibleYn;

    // 이미지 정보
    private String imageUrl;
    private String imageOriginalName;
    private String imageContentType;
    private Long imageSize;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}