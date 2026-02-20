package com.hbk.controller;

import com.hbk.dto.ProductResponse;
import com.hbk.service.ProductService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> list() {
        return productService.list();
    }

    @GetMapping("/{id}")
    public ProductResponse detail(@PathVariable Long id) {
        return productService.getById(id);
    }

    // 🔥 slug로 조회 추가
    @GetMapping("/slug/{slug}")
    public ProductResponse getBySlug(@PathVariable String slug) {
        return productService.getBySlug(slug);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse create(
            @RequestParam @NotBlank String title,
            @RequestParam(required = false) String desc,
            @RequestParam @NotNull Integer price,
            @RequestParam @NotNull Long categoryId,
            @RequestPart("image") MultipartFile image
    ) throws Exception {
        return productService.create(title, desc, price, categoryId, image);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse update(
            @PathVariable Long id,
            @RequestParam @NotBlank String title,
            @RequestParam(required = false) String desc,
            @RequestParam @NotNull Integer price,
            @RequestParam(required = false) Long categoryId,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        return productService.update(id, title, desc, price, categoryId, image);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}