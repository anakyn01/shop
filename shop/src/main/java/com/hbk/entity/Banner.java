package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners",
        indexes = {
                @Index(name = "idx_banners_sort_order", columnList = "sort_order"),
                @Index(name = "idx_banners_visible_yn", columnList = "visible_yn")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FormData: title
    @Column(nullable = false, length = 120)
    private String title;

    // 수정
    @Column(name = "description", nullable = false, length = 500)
    private String desc;

    // ✅ FormData: linkUrl (optional)
    @Column(name = "link_url", length = 500)
    private String linkUrl;

    // ✅ FormData: sortOrder (optional)
    @Column(name = "sort_order")
    private Integer sortOrder;

    // ✅ FormData: visibleYn (default "Y")
    @Column(name = "visible_yn", nullable = false, length = 1)
    private String visibleYn; // "Y" or "N"

    // -----------------------------
    // ✅ 이미지 저장 정보 (DB에는 파일 자체 X)
    // -----------------------------
    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;  // 예: /uploads/banners/uuid.png or https://cdn...

    @Column(name = "image_original_name", length = 255)
    private String imageOriginalName;

    @Column(name = "image_content_type", length = 100)
    private String imageContentType; // image/png, image/jpeg

    @Column(name = "image_size")
    private Long imageSize; // bytes

    // -----------------------------
    // ✅ 생성/수정 시간
    // -----------------------------
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (visibleYn == null || visibleYn.isBlank()) visibleYn = "Y";
        if (sortOrder == null) sortOrder = 0;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
