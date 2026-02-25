package com.hbk.dto;

import com.hbk.entity.SpotItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpotItemRes {

    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private String visibleYn;

    public static SpotItemRes from(SpotItem e) {
        return SpotItemRes.builder()
                .id(e.getId())
                .title(e.getTitle())
                .imageUrl(e.getImageUrl())
                .linkUrl(e.getLinkUrl())
                .sortOrder(e.getSortOrder())
                .visibleYn(e.getVisibleYn())
                .build();
    }
}