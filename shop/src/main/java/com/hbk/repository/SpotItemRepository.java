package com.hbk.repository;

import com.hbk.entity.SpotItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpotItemRepository extends JpaRepository<SpotItem, Long> {

    // ✅ 정렬 조회 (프론트에서 sortOrder 기준 정렬할 수도 있지만
    //    DB에서 바로 정렬해서 가져오는 게 깔끔함)
    List<SpotItem> findAllByOrderBySortOrderAscIdAsc();

    // ✅ 현재 최대 sortOrder 구하기 (신규 등록 시 max + 1 용도)
    @Query("select coalesce(max(s.sortOrder), 0) from SpotItem s")
    int findMaxSortOrder();
}