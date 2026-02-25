package com.hbk.repository;

import com.hbk.entity.TextBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TextBannerRepository extends JpaRepository<TextBanner, Long> {

    //“최대 sortOrder” 조회 메서드 추가
    @Query("select coalesce(max(t.sortOrder), 0) from TextBanner t")
    int findMaxSortOrder();
}
