package com.hbk.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductSizeDTO {
    private Integer size;
    private Integer stock;
}