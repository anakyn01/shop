package com.hbk.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbk.dto.ProductResponseDTO;
import com.hbk.dto.ProductSizeDTO;
import com.hbk.dto.ProductSpecDTO;
import com.hbk.entity.NavMenu;
import com.hbk.entity.Product;
import com.hbk.entity.ProductSize;
import com.hbk.entity.ProductSpec;
import com.hbk.repository.NavMenuRepository;
import com.hbk.repository.ProductRepository;
import com.hbk.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;
    private final FileStorage fileStorage;
    private final NavMenuRepository navMenuRepo;

    // ✅ JSON 파싱용
    private final ObjectMapper objectMapper;

    // ===============================
    // 🔥 slug 생성 로직
    // ===============================
    private String generateSlug(String title) {
        String base = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}가-힣 ]", "")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "-");

        String slug = base;
        int count = 1;

        // 🔥 중복 방지
        while (repo.findBySlug(slug).isPresent()) {
            slug = base + "-" + count++;
        }

        return slug;
    }

    // ===============================
    // ✅ sizes 파싱 + 필수 검증
    // ===============================
    private List<ProductSizeDTO> parseSizes(String sizesJson) {
        if (sizesJson == null || sizesJson.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사이즈는 필수입니다.");
        }
        try {
            List<ProductSizeDTO> list =
                    objectMapper.readValue(sizesJson, new TypeReference<List<ProductSizeDTO>>() {});
            if (list == null || list.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사이즈를 1개 이상 추가하세요.");
            }
            for (ProductSizeDTO s : list) {
                if (s.getSize() == null || s.getSize() <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사이즈 값이 올바르지 않습니다.");
                }
                if (s.getStock() == null || s.getStock() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "재고는 0 이상이어야 합니다.");
                }
            }
            return list;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사이즈 파싱 실패");
        }
    }

    // ===============================
    // ✅ specs 파싱 + 필수 검증
    // ===============================
    private List<ProductSpecDTO> parseSpecs(String specsJson) {
        if (specsJson == null || specsJson.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품정보고시는 필수입니다.");
        }
        try {
            List<ProductSpecDTO> list =
                    objectMapper.readValue(specsJson, new TypeReference<List<ProductSpecDTO>>() {});
            if (list == null || list.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품정보고시를 1개 이상 추가하세요.");
            }
            for (ProductSpecDTO s : list) {
                if (s.getLabel() == null || s.getLabel().trim().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품정보고시 항목명을 입력하세요.");
                }
                if (s.getValue() == null || s.getValue().trim().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품정보고시 내용을 입력하세요.");
                }
            }
            return list;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품정보고시 파싱 실패");
        }
    }

    // ===============================
    // ✅ 전체 목록
    // ===============================
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> list() {
        return repo.findAll().stream()
                .map(ProductResponseDTO::from)
                .toList();
    }

    // ===============================
    // ✅ 단건 조회 (id)
    // ===============================
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        Product e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "상품이 없습니다. id=" + id
                ));
        return ProductResponseDTO.from(e);
    }

    // ===============================
    // ✅ slug 조회
    // ===============================
    @Transactional(readOnly = true)
    public ProductResponseDTO getBySlug(String slug) {
        Product e = repo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "상품이 없습니다. slug=" + slug
                ));
        return ProductResponseDTO.from(e);
    }

    // ===============================
    // ✅ 생성
    // ===============================
    @Transactional
    public ProductResponseDTO create(
            String title,
            String desc,
            Integer price,
            Long categoryId,
            String sizesJson,
            String specsJson,
            MultipartFile image
    ) throws Exception {

        if (title == null || title.isBlank())
            throw new IllegalArgumentException("상품명은 필수입니다.");
        if (price == null || price <= 0)
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        if (categoryId == null)
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        if (image == null || image.isEmpty())
            throw new IllegalArgumentException("이미지를 선택하세요.");

        // ✅ 필수 파싱/검증
        List<ProductSizeDTO> sizes = parseSizes(sizesJson);
        List<ProductSpecDTO> specs = parseSpecs(specsJson);

        NavMenu category = navMenuRepo.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "카테고리가 존재하지 않습니다."
                ));

        var stored = fileStorage.save(image);

        // 🔥 slug 생성
        String slug = generateSlug(title);

        Product product = Product.builder()
                .title(title)
                .desc(desc)
                .price(price)
                .imageUrl(stored.url())
                .imagePath(stored.filePath())
                .slug(slug)
                .category(category)
                .build();

        // ✅ sizes 세팅
        product.clearSizes();
        for (ProductSizeDTO s : sizes) {
            product.addSize(ProductSize.builder()
                    .size(s.getSize())
                    .stock(s.getStock())
                    .build());
        }

        // ✅ specs 세팅
        product.clearSpecs();
        for (ProductSpecDTO s : specs) {
            product.addSpec(ProductSpec.builder()
                    .label(s.getLabel().trim())
                    .value(s.getValue().trim())
                    .build());
        }

        Product saved = repo.save(product);
        return ProductResponseDTO.from(saved);
    }

    // ===============================
    // ✅ 수정
    // ===============================
    @Transactional
    public ProductResponseDTO update(
            Long id,
            String title,
            String desc,
            Integer price,
            Long categoryId,
            String sizesJson,
            String specsJson,
            MultipartFile image
    ) throws Exception {

        Product e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. id=" + id));

        // slug가 없으면 강제로 생성
        if (e.getSlug() == null || e.getSlug().isBlank()) {
            String baseTitle = (title != null && !title.isBlank()) ? title : e.getTitle();
            e.setSlug(generateSlug(baseTitle));
        }

        if (title != null && !title.isBlank()) {
            e.setTitle(title);
            e.setSlug(generateSlug(title));
        }

        if (desc != null) e.setDesc(desc);
        if (price != null && price > 0) e.setPrice(price);

        if (categoryId != null) {
            NavMenu category = navMenuRepo.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
            e.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            fileStorage.deleteByPath(e.getImagePath());
            var stored = fileStorage.save(image);
            e.setImageUrl(stored.url());
            e.setImagePath(stored.filePath());
        }

        // ✅ sizes/specs는 쇼핑몰 필수 → update도 필수로 강제
        List<ProductSizeDTO> sizes = parseSizes(sizesJson);
        List<ProductSpecDTO> specs = parseSpecs(specsJson);

        e.clearSizes();
        for (ProductSizeDTO s : sizes) {
            e.addSize(ProductSize.builder()
                    .size(s.getSize())
                    .stock(s.getStock())
                    .build());
        }

        e.clearSpecs();
        for (ProductSpecDTO s : specs) {
            e.addSpec(ProductSpec.builder()
                    .label(s.getLabel().trim())
                    .value(s.getValue().trim())
                    .build());
        }

        Product saved = repo.save(e);
        return ProductResponseDTO.from(saved);
    }

    // ===============================
    // ✅ 삭제
    // ===============================
    @Transactional
    public void delete(Long id) {
        Product e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "상품이 없습니다. id=" + id
                ));

        fileStorage.deleteByPath(e.getImagePath());
        repo.delete(e);
    }
}