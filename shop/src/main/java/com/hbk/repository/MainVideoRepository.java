package com.hbk.repository;

import com.hbk.entity.MainVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MainVideoRepository extends JpaRepository<MainVideo, Long> {

    /** ✅ 항상 1개 유지 구조: 최신 1개 가져오기 */
    Optional<MainVideo> findTopByOrderByIdDesc();
}