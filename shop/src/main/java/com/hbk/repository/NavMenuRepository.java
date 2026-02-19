package com.hbk.repository;

import com.hbk.entity.NavMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//NavMenu 엔티티를 관리 PK 타입은 Long 기본 CRUD 자동 제공 save(), findById()
//findAll(), deleteById()
public interface NavMenuRepository extends JpaRepository<NavMenu, Long> {
    //1차 메뉴 조회
    List<NavMenu> findByParentIsNullOrderBySortOrderAscIdAsc();
    //parent가 null인 것만 조회 sortOrder 오름차순 id 오름차순

    //✅ 2️⃣ 특정 부모의 자식 조회
    List<NavMenu> findByParentIsOrderBySortOrderAscIdAsc(Long parentId);
    //parent_id = ? 인 메뉴 조회 즉 특정 메뉴의 2차 또는 3차 조회 정렬 포함

    //✅ 3️⃣ 루트(1차)의 최대 정렬값 조회
    @Query("select coalesce(max(n.sortOrder),0) from NavMenu n where n.parent is null")
    int maxSortOrderRoot();
    /*
coalesce kow·uh·les : NULL이면 0으로 바꿔라
1차 메뉴 중에서  sortOrder 최대값 조회 없으면 0 반환
JPQL = Java Persistence Query Language
👉 JPA에서 사용하는 객체 지향 쿼리 언어
DB 테이블이 아니라 "엔티티 객체"를 대상으로 조회
    * */
@Query("select coalesce(max(n.sortOrder),0) from NavMenu n where n.parent.id = :parentId")
int maxSortOrderByParent(Long parentId);
//특정 부모 밑 자식들의 최대 sortOrder 조회
}
