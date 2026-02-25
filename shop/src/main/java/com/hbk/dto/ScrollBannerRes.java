// ✅ ScrollBannerRes.java
package com.hbk.dto;

import com.hbk.entity.ScrollBanner;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScrollBannerRes {

    private Long id;
    private String title;
    private String imageUrl;

    private String linkUrl;

    private String buttonText;
    private String buttonLinkUrl;

    private Integer sortOrder;
    private String visibleYn;

    public static ScrollBannerRes from(ScrollBanner e) {
        return ScrollBannerRes.builder()
                .id(e.getId())
                .title(e.getTitle())
                .imageUrl(e.getImageUrl())
                .linkUrl(e.getLinkUrl())
                .buttonText(e.getButtonText())
                .buttonLinkUrl(e.getButtonLinkUrl())
                .sortOrder(e.getSortOrder())
                .visibleYn(e.getVisibleYn())
                .build();
    }
}