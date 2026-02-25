package com.hbk.repository;

import com.hbk.entity.ScrollBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScrollBannerRepository extends JpaRepository<ScrollBanner, Long> {

    @Query("select coalesce(max(s.sortOrder),0) from ScrollBanner s")
    int findMaxSortOrder();
}