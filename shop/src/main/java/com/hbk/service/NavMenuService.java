package com.hbk.service;

import com.hbk.dto.NavMenuResponseDTO;
import com.hbk.entity.NavMenu;
import com.hbk.repository.NavMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //👉 final 필드 생성자 자동 생성
@Transactional
public class NavMenuService {


    private final NavMenuRepository navMenuRepository; //di

    @Transactional(readOnly = true)
    public List<NavMenuResponseDTO> tree(){ //트리전체 조회
        //1차 메뉴조회
        List<NavMenu> roots = navMenuRepository.findByParentIsNullOrderBySortOrderAscIdAsc();
        //👉 각 루트 메뉴를 재귀적으로 트리 DTO 변환
        return roots.stream().map(this::toTreeDto).collect(Collectors.toList());

    }

    //✅ 2️⃣ 메뉴 생성
    public NavMenuResponseDTO create(NavMenuResponseDTO req){
        //이름검증
        String name = req.getName() == null ? "" : req.getName().trim();
        //👉 null 방지 + 공백 제거
        if(name.isEmpty()) throw new IllegalArgumentException("name is required");
    }

    private NavMenuResponseDTO toFlatDto(NavMenu n){
        return NavMenuResponseDTO.builder().build();
    }

    private NavMenuResponseDTO toTreeDto(NavMenu n){
        NavMenuResponseDTO dto = toFlatDto(n);
        return dto;
    }
}
