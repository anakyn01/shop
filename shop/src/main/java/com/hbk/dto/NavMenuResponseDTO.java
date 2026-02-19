package com.hbk.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NavMenuRequestDTO {

    private String name, path, visibleYn;
    private Long parentId;
    private Integer sortOrder;

}
