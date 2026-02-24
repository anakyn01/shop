package com.hbk.repository;

import com.hbk.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    // ✅ 노출된 배너만 정렬순서대로 조회
    List<Banner> findByVisibleYnOrderBySortOrderAsc(String visibleYn);

    // ✅ 전체 정렬 조회 (관리자용)
    List<Banner> findAllByOrderBySortOrderAsc();

}