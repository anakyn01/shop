package com.hbk.controller;

import com.hbk.dto.ProductResponseDTO;
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
    public List<ProductResponseDTO> list() {
        return productService.list();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO detail(@PathVariable Long id) {
        return productService.getById(id);
    }

    // 🔥 slug로 조회
    @GetMapping("/slug/{slug}")
    public ProductResponseDTO getBySlug(@PathVariable String slug) {
        return productService.getBySlug(slug);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO create(
            @RequestParam @NotBlank String title,
            @RequestParam(required = false) String desc,
            @RequestParam @NotNull Integer price,
            @RequestParam @NotNull Long categoryId,

            // ✅ sizes/specs (JSON 문자열)
            @RequestParam @NotBlank String sizes,
            @RequestParam @NotBlank String specs,

            @RequestPart("image") MultipartFile image
    ) throws Exception {
        return productService.create(title, desc, price, categoryId, sizes, specs, image);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO update(
            @PathVariable Long id,
            @RequestParam @NotBlank String title,
            @RequestParam(required = false) String desc,
            @RequestParam @NotNull Integer price,
            @RequestParam(required = false) Long categoryId,

            // ✅ sizes/specs (JSON 문자열)
            @RequestParam @NotBlank String sizes,
            @RequestParam @NotBlank String specs,

            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        return productService.update(id, title, desc, price, categoryId, sizes, specs, image);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}