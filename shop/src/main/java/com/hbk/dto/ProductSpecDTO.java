package com.hbk.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductSpecDTO {
    private String label;
    private String value;
}