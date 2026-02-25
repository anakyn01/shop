package com.hbk.repository;

import com.hbk.entity.FooterCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FooterCategoryRepository extends JpaRepository<FooterCategory, Long> {
    List<FooterCategory> findAllByOrderBySortOrderAscIdAsc();
}