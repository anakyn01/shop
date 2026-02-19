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

    private final CategoryRepository categoryRepo;//DB 접근용 Repository

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllHierarchy(){
        //전체 카테고리 계층 조회, 읽기 전용 트랜잭션
List<Category> roots = categoryRepo.findByParentCategoryIsNull();
//부모 없는 1차 카테고리 조회
        return roots.stream().map(CategoryResponse::from)
                .collect(Collectors.toList());
        //엔티티를 DTO로 변환하여 리스트 반환
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) { //ID로 단일 카테고리 조회
Category category = categoryRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다. id=" + id));
return CategoryResponse.from(category); //DTO로 변환 후 반환
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) { //카테고리 생성, 쓰기 트랜잭션
        Category parent = null; //부모 카테고리 초기화
if (request.getParentId() != null) {
    parent = categoryRepo.findById(request.getParentId())
.orElseThrow(() -> new IllegalArgumentException("부모 카테고리가 존재하지 않습니다. id=" + request.getParentId()));
}
        Category category = Category.builder()
                .name(request.getName()).parentCategory(parent)
                .build();
//새 카테고리 엔티티 생성
        Category saved = categoryRepo.save(category);
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request){
        Category category = categoryRepo.findById(id)
                .orElseThrow(()->new IllegalArgumentException("카테고리가 존재하지 않습니다. id=" + id));
        category.setName(request.getName());//이름 변경
        if (request.getParentId() != null) {
Category parent = categoryRepo.findById(request.getParentId())
        .orElseThrow(()->new IllegalArgumentException("부모 카테고리가 존재하지 않습니다. id=" + request.getParentId()));
            category.setParentCategory(parent); // 이 줄이 빠져 있었음
        }else{
            category.setParentCategory(null); // 1차 카테고리로 변경
        }

        Category saved = categoryRepo.save(category);
        return CategoryResponse.from(saved);
    }

    @Transactional
    public void delete(Long id){
        Category category = categoryRepo.findById(id)
                .orElseThrow(()->new IllegalArgumentException("카테고리가 존재하지 않습니다. id=" + id));
        categoryRepo.delete(category);

    }


}
