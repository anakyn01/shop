package com.hbk.dto;

import com.hbk.dto.ProductResponseDTO;
import com.hbk.dto.ProductSizeDTO;
import com.hbk.dto.ProductSpecDTO;
import com.hbk.entity.Product;
import com.hbk.entity.ProductSize;
import com.hbk.entity.ProductSpec;

import java.util.List;

public class ProductMapper {

    //메서드 static인 이유: 객체 생성 없이 바로 사용 가능 util 성격
    public static ProductResponseDTO toDto(Product p){
        //사이즈 변환부분
        List<ProductSizeDTO> sizes = p.getSizes().stream()
                .map(s -> ProductSizeDTO.builder()
                .size(s.getSize()).stock(s.getStock()).build()).toList();

        //상품고시 변환부분
        List<ProductSpecDTO> specs = p.getSpecs().stream()
                .map(s -> ProductSpecDTO.builder()
                        .label(s.getLabel()).value(s.getValue()).build()).toList();

        return ProductResponseDTO.builder()
.id(p.getId()).title(p.getTitle()).desc(p.getDesc()).price(p.getPrice())
                .imageUrl(p.getImageUrl()).slug(p.getSlug())
                //카테고리 null 안전 처리
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                //카테고리가 있으면 ID 반환 없으면 null
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                //카테고리 이름도 동일 처리
                .sizes(sizes).specs(specs)
                .build();
    }

}