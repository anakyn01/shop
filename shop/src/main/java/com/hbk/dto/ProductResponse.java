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

    // ✅ NavMenu 기준
    private Long categoryId;
    private String categoryName;

    public static ProductResponse from(Product e) {
        return ProductResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .desc(e.getDesc())
                .price(e.getPrice())
                .imageUrl(e.getImageUrl())
                .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
                .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
                .build();
    }
}