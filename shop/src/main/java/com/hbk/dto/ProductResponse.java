package com.hbk.dto;

import com.hbk.entity.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String title;
    private String desc;
    private Integer price;
    private String imageUrl;

    // Category 객체 기준
    private Long primaryCategoryId;
    private String primaryCategoryName;
    private Long secondaryCategoryId;
    private String secondaryCategoryName;

    public static ProductResponse from(Product e) {
        return ProductResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .desc(e.getDesc())
                .price(e.getPrice())
                .imageUrl(e.getImageUrl())
                .primaryCategoryId(e.getPrimaryCategory() != null ? e.getPrimaryCategory().getId() : null)
                .primaryCategoryName(e.getPrimaryCategory() != null ? e.getPrimaryCategory().getName() : null)
                .secondaryCategoryId(e.getSecondaryCategory() != null ? e.getSecondaryCategory().getId() : null)
                .secondaryCategoryName(e.getSecondaryCategory() != null ? e.getSecondaryCategory().getName() : null)
                .build();
    }
}
