package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="footer_text")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FooterText {

    @Id
    private Long id; // 항상 1로 고정 추천

    @Lob
    private String paragraph1;

    @Lob
    private String paragraph2;
}