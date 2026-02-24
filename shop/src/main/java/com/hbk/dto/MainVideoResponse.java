package com.hbk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainVideoResponse {
    private Long id;
    private String videoUrl;
    private String title;
    private String subtitle;

    private String btn1Text;
    private String btn1Link;
    private String btn2Text;
    private String btn2Link;
}