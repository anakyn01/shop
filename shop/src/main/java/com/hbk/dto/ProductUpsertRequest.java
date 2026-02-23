package com.hbk.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductUpsertRequest {
    private String title;
    private String desc;
    private Integer price;
    private Long categoryId;

    private List<ProductSizeDTO> sizes;
    private List<ProductSpecDTO> specs;
}