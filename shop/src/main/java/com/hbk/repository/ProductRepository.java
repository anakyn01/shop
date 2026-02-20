package com.hbk.repository;

import com.hbk.entity.Product;
import com.hbk.entity.NavMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(NavMenu category);

    List<Product> findByCategory_Id(Long categoryId);

    // 🔥 추가 (slug 조회용)
    Optional<Product> findBySlug(String slug);
}