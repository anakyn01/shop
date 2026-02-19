package com.hbk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    private String name; // 카테고리 이름
    private Long parentId; // 부모 카테고리 ID (null이면 1차 카테고리)
}
