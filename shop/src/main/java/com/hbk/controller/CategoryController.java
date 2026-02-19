package com.hbk.controller;

import com.hbk.dto.CategoryRequest;
import com.hbk.dto.CategoryResponse;
import com.hbk.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CategoryController {

    private final CategoryService service;

    // ✅ 전체 카테고리 조회
    @GetMapping
    public List<CategoryResponse> list() {
        return service.getAllHierarchy();
    }

    // ✅ 단일 카테고리 조회
    @GetMapping("/{id}")
    public CategoryResponse detail(@PathVariable Long id) {
        return service.getById(id);
    }

    // ✅ 생성
    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest req) {
        return service.create(req);
    }

    // ✅ 수정
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @RequestBody CategoryRequest req) {
        return service.update(id, req);
    }

    // ✅ 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
