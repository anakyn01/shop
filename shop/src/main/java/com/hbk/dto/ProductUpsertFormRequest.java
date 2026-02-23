package com.hbk.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductUpsertFormRequest {
    private String title;
    private String desc;
    private Integer price;
    private Long categoryId;

    /**
     * 프론트에서 FormData로 보내는 JSON 문자열
     * fd.append("sizes", JSON.stringify([...]))
     * fd.append("specs", JSON.stringify([...]))
     */
    private String sizes;
    private String specs;
}