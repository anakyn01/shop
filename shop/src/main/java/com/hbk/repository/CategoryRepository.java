package com.hbk.repository;

import com.hbk.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 추가적인 카테고리 관련 쿼리 메서드를 작성할 수 있습니다.
    // 1차 카테고리만 조회 (parentCategory가 null)
    List<Category> findByParentCategoryIsNull();
}
