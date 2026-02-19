package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String desc;

    @Column(nullable=false)
    private Integer price;

    // DB에는 URL 저장 (프론트에서 p.imageUrl로 사용)
    @Column(nullable=false, length = 300)
    private String imageUrl;

    // 실제 저장된 파일명(삭제할 때 필요)
    @Column(nullable=false, length = 300)
    private String imagePath;

    // 1차 카테고리 ID (null 허용)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 추가
    @JoinColumn(name = "primary_category", referencedColumnName= "id", nullable = true)
    private Category primaryCategory;

    // 2차 카테고리 ID (null 허용)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 추가
    @JoinColumn(name = "secondary_category", referencedColumnName= "id", nullable = true)
    private Category secondaryCategory;
}
