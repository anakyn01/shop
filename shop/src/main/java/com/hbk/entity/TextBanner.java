package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "TextBanner")
@Table(name = "text_banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name="description", nullable = false, length = 500)
    private String desc;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String linkUrl;

    @Column(name="sort_order")
    private Integer sortOrder;

    @Column(nullable = false, length = 1)
    private String visibleYn;


}
