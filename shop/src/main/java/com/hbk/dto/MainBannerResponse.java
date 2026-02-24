package com.hbk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainBannerResponse {
    private Long id;
    private String imageUrl;
    private String linkUrl;
}
