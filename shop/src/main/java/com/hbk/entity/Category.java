package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // --------------------------
    // 2차 카테고리 (자식 카테고리) 목록
    // --------------------------
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children;

    // --------------------------
    // 1차 카테고리 (부모 카테고리)
    // --------------------------
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    // --------------------------
    // 1차 카테고리로 연결된 상품
    // --------------------------
    @OneToMany(mappedBy = "primaryCategory")
    private List<Product> primaryProducts;

    // --------------------------
    // 2차 카테고리로 연결된 상품
    // --------------------------
    @OneToMany(mappedBy = "secondaryCategory")
    private List<Product> secondaryProducts;
}
