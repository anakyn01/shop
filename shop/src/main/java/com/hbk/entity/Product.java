package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String desc;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 300)
    private String imageUrl;

    @Column(nullable = false, length = 300)
    private String imagePath;

    // ✅ 🔥 slug (유니크, 필수)
    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    // ✅ NavMenu 기준 (카테고리)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private NavMenu category;
}