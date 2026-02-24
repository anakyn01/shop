package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "main_video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 업로드된 영상 URL (예: /uploads/main-video/xxxx.mp4) */
    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    /** 화면 h1 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 화면 p */
    @Column(name = "subtitle", nullable = false, length = 500)
    private String subtitle;

    /** 버튼1 */
    @Column(name = "btn1_text", length = 100)
    private String btn1Text;

    @Column(name = "btn1_link", length = 500)
    private String btn1Link;

    /** 버튼2 */
    @Column(name = "btn2_text", length = 100)
    private String btn2Text;

    @Column(name = "btn2_link", length = 500)
    private String btn2Link;

    /** 생성/수정 시간 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}