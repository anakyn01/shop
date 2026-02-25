package com.hbk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FooterCategoryCreateReq {
    private String title;
    private Integer sortOrder;
    private String visibleYn; // Y/N
}