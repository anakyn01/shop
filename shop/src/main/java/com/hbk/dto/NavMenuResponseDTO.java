package com.hbk.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NavMenuResponseDTO {
    private Long id;
    private String name, path, visibleYn;
    private Integer sortOrder, depth;

    private List<NavMenuResponseDTO> children = new ArrayList<>();

}
