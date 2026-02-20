package com.hbk.service;

import com.hbk.dto.CategoryRequest;
import com.hbk.dto.CategoryResponse;
import com.hbk.entity.Category;
import com.hbk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;

    // ✅ 전체 카테고리 계층 조회
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllHierarchy() {
        List<Category> roots = categoryRepo.findByParentCategoryIsNull();
        return roots.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    // ✅ 단일 카테고리 조회
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다. id=" + id));
        return CategoryResponse.from(category);
    }

    // ✅ 카테고리 생성
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepo.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리가 존재하지 않습니다. id=" + request.getParentId()));
        }

        Category category = Category.builder()
                .name(request.getName())
                .parentCategory(parent)
                .build();

        Category saved = categoryRepo.save(category);
        return CategoryResponse.from(saved);
    }

    // ✅ 카테고리 수정
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다. id=" + id));

        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepo.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리가 존재하지 않습니다. id=" + request.getParentId()));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category saved = categoryRepo.save(category);
        return CategoryResponse.from(saved);
    }

    // ✅ 카테고리 삭제
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다. id=" + id));
        categoryRepo.delete(category);
    }
}