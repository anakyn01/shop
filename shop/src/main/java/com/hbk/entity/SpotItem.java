package com.hbk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spot_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String imageUrl;

    private String linkUrl;

    private Integer sortOrder;

    private String visibleYn;
}
