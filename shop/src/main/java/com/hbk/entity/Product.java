package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

    // ✅ slug (유니크, 필수)
    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    // ✅ 카테고리 (NavMenu)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private NavMenu category;

    // =========================
    // ✅ 사이즈/재고 (필수)
    // =========================
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("size ASC")
    @Builder.Default
    private List<ProductSize> sizes = new ArrayList<>();

    // =========================
    // ✅ 상품정보고시 (필수)
    // =========================
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("id ASC")
    @Builder.Default
    private List<ProductSpec> specs = new ArrayList<>();

    // -------------------------
    // 편의 메서드 (양방향 세팅)
    // -------------------------
    public void addSize(ProductSize s) {
        if (s == null) return;
        s.setProduct(this);
        this.sizes.add(s);
    }

    public void clearSizes() {
        for (ProductSize s : this.sizes) s.setProduct(null);
        this.sizes.clear();
    }

    public void addSpec(ProductSpec s) {
        if (s == null) return;
        s.setProduct(this);
        this.specs.add(s);
    }

    public void clearSpecs() {
        for (ProductSpec s : this.specs) s.setProduct(null);
        this.specs.clear();
    }
}