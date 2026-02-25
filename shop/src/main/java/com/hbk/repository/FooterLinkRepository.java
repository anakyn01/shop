package com.hbk.repository;

import com.hbk.entity.FooterLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FooterLinkRepository extends JpaRepository<FooterLink, Long> {
    List<FooterLink> findByCategoryIdOrderBySortOrderAscIdAsc(Long categoryId);
    List<FooterLink> findAllByOrderBySortOrderAscIdAsc();
}