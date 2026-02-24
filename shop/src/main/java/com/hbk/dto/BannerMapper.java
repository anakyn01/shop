package com.hbk.dto;

import com.hbk.entity.Banner;

public class BannerMapper {

    public static BannerResponse toResponse(Banner b) {
        return BannerResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .desc(b.getDesc())
                .linkUrl(b.getLinkUrl())
                .sortOrder(b.getSortOrder())
                .visibleYn(b.getVisibleYn())
                .imageUrl(b.getImageUrl())
                .imageOriginalName(b.getImageOriginalName())
                .imageContentType(b.getImageContentType())
                .imageSize(b.getImageSize())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
