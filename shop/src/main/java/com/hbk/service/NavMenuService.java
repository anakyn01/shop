package com.hbk.service;

import com.hbk.dto.NavMenuResponseDTO;
import com.hbk.entity.NavMenu;
import com.hbk.repository.NavMenuRepository;
import jakarta.persistence.EntityNotFoundException;
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

        //✅ parent / depth 계산
        NavMenu parent = null; //parent 기본값 = null (루트 메뉴)
        int depth = 1; //depth 기본값 = 1 (1차 메뉴)

        //parentId가 존재하면 → 하위 메뉴 생성
        if(req.getParentId() != null) {
//부모 메뉴 조회
            parent = navMenuRepository.findById(req.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("parent not found :" + req.getParentId()));
//부모가 없으면 예외 발생
            depth = parent.getDepth() + 1;
//부모 depth + 1 부모가 1차면 → 자식은 2차 부모가 2차면 → 자식은 3차
            if (depth > 3) throw new IllegalArgumentException("depth max is 3");
        }
            //최대 3단계까지만 허용
            int sortOrder = (req.getSortOrder() != null)
                    ? req.getSortOrder()
                    //sortOrder가 직접 들어왔는지 확인 들어왔다면 → 그대로 사용
                    : (parent == null
                    //없으면 자동 계산
                    ? navMenuRepository.maxSortOrderRoot() + 1
                    //parent가 null이면 → 루트 메뉴 루트 최대 정렬값 + 1
                    : navMenuRepository.maxSortOrderByParent(parent.getId()) + 1);
            //부모가 있으면 해당 부모 아래의 최대 sortOrder + 1

            String visibleYn = (req.getVisibleYn() == null || req.getVisibleYn().isBlank())
                    //visibleYn이 null 이거나 빈값이면
                    ? "Y"
                    : req.getVisibleYn().trim().toUpperCase();//값이 있다면 공백 제거 대문자로 변환

            String path = req.getPath(); //path가져오기

            if (path != null) {
                path = path.trim();//공백 제거
                if(!path.isEmpty() && !path.startsWith("/")) path = "/" + path;
                //비어있지 않고 "/"로 시작하지 않으면  앞에 "/" 자동 붙임
            }

            NavMenu saved = navMenuRepository.save(
                    NavMenu.builder()
.name(name).path(path).depth(depth).sortOrder(sortOrder).visibleYn(visibleYn).parent(parent).build()
            );
            return toFlatDto(saved);
    }
   public void delete(Long id) {
            NavMenu menu = navMenuRepository.findById(id)
                    .orElseThrow(()-> new EntityNotFoundException("menu not found: " + id));
            navMenuRepository.delete(menu);
        }


    private NavMenuResponseDTO toFlatDto(NavMenu n){
        return NavMenuResponseDTO.builder()
.id(n.getId()).name(n.getName()).path(n.getPath()).depth(n.getDepth()).sortOrder(n.getSortOrder())
                .visibleYn(n.getVisibleYn())
                .build();
    }

    private NavMenuResponseDTO toTreeDto(NavMenu n){
        NavMenuResponseDTO dto = toFlatDto(n);
        if(n.getChildren() != null && !n.getChildren().isEmpty()){
            dto.setChildren(n.getChildren().stream().map(this::toTreeDto).collect(
                  Collectors.toList()
            ));
        }
        return dto;
    }
}
