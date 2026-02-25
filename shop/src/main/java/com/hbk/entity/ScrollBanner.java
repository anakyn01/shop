package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="scroll_banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrollBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제목
    @Column(nullable = false, length = 120)
    private String title;

    // 이미지 경로
    @Column(length = 255)
    private String imageUrl;

    // 카드 전체 클릭 링크
    @Column(length = 255)
    private String linkUrl;

    // 버튼 텍스트 (기본 "구매하기")
    @Column(length = 50)
    private String buttonText;

    // 버튼 전용 링크
    @Column(length = 255)
    private String buttonLinkUrl;

    // 정렬 순서
    @Column
    private Integer sortOrder;

    // 노출 여부 Y/N
    @Column(length = 1)
    private String visibleYn;

}
