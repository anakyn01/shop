package com.hbk.dto;

import com.hbk.entity.Category;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private Long parentId;
    private List<CategoryResponse> children;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .children(category.getChildren() != null ?
                        category.getChildren().stream()
                                .map(CategoryResponse::from)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}