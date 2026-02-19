package com.hbk.repository;

import com.hbk.entity.Product;
import com.hbk.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1차 카테고리로 상품 리스트 조회
    List<Product> findByPrimaryCategory(Category primaryCategory);

    // 2차 카테고리로 상품 리스트 조회
    List<Product> findBySecondaryCategory(Category secondaryCategory);

    // 1차 + 2차 카테고리 조회
    List<Product> findByPrimaryCategoryAndSecondaryCategory(
            Category primaryCategory,
            Category secondaryCategory
    );

    // 가격 범위 조회
    List<Product> findByPriceBetween(Integer minPrice, Integer maxPrice);

    // ⭐ ID 기준으로 바로 조회하고 싶다면 (추천)
    List<Product> findByPrimaryCategory_Id(Long primaryCategoryId);

    List<Product> findBySecondaryCategory_Id(Long secondaryCategoryId);

    List<Product> findByPrimaryCategory_IdAndSecondaryCategory_Id(
            Long primaryCategoryId,
            Long secondaryCategoryId
    );
}
