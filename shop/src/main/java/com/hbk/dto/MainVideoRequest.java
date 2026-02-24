package com.hbk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainVideoRequest {
    private String title;
    private String subtitle;

    private String btn1Text;
    private String btn1Link;
    private String btn2Text;
    private String btn2Link;
}