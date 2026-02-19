package com.hbk.dto;

import com.hbk.entity.Category;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name; // 카테고리 이름
    private Long parentId; // 부모 카테고리 ID (null이면 1차 카테고리)
    private List<CategoryResponse> children; // 2차 카테고리 목록

    //정적 메서드 정의 Category 엔티티 객체를 받아서 CategoryResponse DTO 객체로 변환
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder().
id(category.getId()).name(category.getName())
.parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
//부모 카테고리가 존재하면 그 ID를 넣고, 없으면 null
.children(category.getChildren() != null ? category.getChildren().stream().map(CategoryResponse::from)
//category.getChildren()이 null이 아니면:
// 1.스트림 생성 .stream()
// 2.각 자식 카테고리를 CategoryResponse.from()으로 DTO 변환
// 3.리스트로 수집 .collect(Collectors.toList())
//없으면 null
        .collect(Collectors.toList()) : null).build();
    }
}
