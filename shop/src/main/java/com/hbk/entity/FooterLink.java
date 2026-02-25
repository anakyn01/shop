package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "footer_links")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FooterLink {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id", nullable=false)
    private FooterCategory category;

    @Column(nullable=false, length=80)
    private String label; // 2차 메뉴명(a 텍스트)

    @Column(nullable=false, length=255)
    private String url; // 2차 메뉴 URL

    @Column
    private Integer sortOrder;

    @Column(length=1)
    private String visibleYn; // Y/N
}