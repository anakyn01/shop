package com.hbk.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SpotItemCreateReq {

    // ✅ 필수
    private String title;

    // ✅ 선택: 아이콘 클릭 링크
    private String linkUrl;

    // ✅ 선택: 정렬
    private Integer sortOrder;

    // ✅ 선택: 노출여부(Y/N) - 없으면 서비스에서 "Y" 기본값 권장
    private String visibleYn;

    // ✅ 선택: 이미지 파일 (FormData key = "image")
    private MultipartFile image;
}