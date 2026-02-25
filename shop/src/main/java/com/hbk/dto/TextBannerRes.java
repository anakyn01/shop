package com.hbk.dto;

import com.hbk.entity.TextBanner;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextBannerRes {

    private Long id;
    private String title;
    private String desc;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private String visibleYn; // "Y" | "N"

    public static TextBannerRes from(TextBanner e) {
        return TextBannerRes.builder()
                .id(e.getId())
                .title(e.getTitle())
                .desc(e.getDesc())
                .imageUrl(e.getImageUrl())
                .linkUrl(e.getLinkUrl())
                .sortOrder(e.getSortOrder())
                .visibleYn(e.getVisibleYn())
                .build();
    }
}
