package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "footer_categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FooterCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=80)
    private String title; // 1차 카테고리명(h6)

    @Column
    private Integer sortOrder;

    @Column(length=1)
    private String visibleYn; // Y/N
}