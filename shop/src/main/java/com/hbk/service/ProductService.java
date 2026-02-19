package com.hbk.service;

import com.hbk.dto.ProductResponse;
import com.hbk.entity.Product;
import com.hbk.entity.Category;
import com.hbk.repository.ProductRepository;
import com.hbk.repository.CategoryRepository;
import com.hbk.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;
    private final FileStorage fileStorage;
    private final CategoryRepository categoryRepo;

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return repo.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. id=" + id));
        return ProductResponse.from(e);
    }

    @Transactional
    public ProductResponse create(String title, String desc, Integer price,
                                  Long primaryCategoryId, Long secondaryCategoryId,
                                  MultipartFile image) throws Exception {

        if (title == null || title.isBlank()) throw new IllegalArgumentException("상품명은 필수입니다.");
        if (price == null || price <= 0) throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        if (image == null || image.isEmpty()) throw new IllegalArgumentException("이미지를 선택하세요.");

        // Category 조회
        Category primaryCat = categoryRepo.findById(primaryCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("1차 카테고리가 존재하지 않습니다."));
        Category secondaryCat = categoryRepo.findById(secondaryCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("2차 카테고리가 존재하지 않습니다."));

        var stored = fileStorage.save(image);

        Product saved = repo.save(Product.builder()
                .title(title)
                .desc(desc)
                .price(price)
                .imageUrl(stored.url())
                .imagePath(stored.filePath())
                .primaryCategory(primaryCat)
                .secondaryCategory(secondaryCat)
                .build());

        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, String title, String desc, Integer price,
                                  Long primaryCategoryId, Long secondaryCategoryId,
                                  MultipartFile image) throws Exception {

        Product e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. id=" + id));

        if (title != null && !title.isBlank()) e.setTitle(title);
        if (desc != null) e.setDesc(desc);
        if (price != null && price > 0) e.setPrice(price);

        if (primaryCategoryId != null) {
            Category primaryCat = categoryRepo.findById(primaryCategoryId)
                    .orElseThrow(() -> new IllegalArgumentException("1차 카테고리가 존재하지 않습니다."));
            e.setPrimaryCategory(primaryCat);
        }

        if (secondaryCategoryId != null) {
            Category secondaryCat = categoryRepo.findById(secondaryCategoryId)
                    .orElseThrow(() -> new IllegalArgumentException("2차 카테고리가 존재하지 않습니다."));
            e.setSecondaryCategory(secondaryCat);
        }

        if (image != null && !image.isEmpty()) {
            fileStorage.deleteByPath(e.getImagePath());
            var stored = fileStorage.save(image);
            e.setImageUrl(stored.url());
            e.setImagePath(stored.filePath());
        }

        Product saved = repo.save(e);
        return ProductResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Product e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. id=" + id));

        fileStorage.deleteByPath(e.getImagePath());
        repo.delete(e);
    }
}
