// ✅ ScrollBannerCreateReq.java
package com.hbk.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ScrollBannerCreateReq {

    // 필수
    private String title;

    // 선택: 카드 클릭 링크
    private String linkUrl;

    // 선택: 버튼 텍스트 (없으면 서비스에서 "구매하기" 기본값 처리 권장)
    private String buttonText;

    // 선택: 버튼 클릭 링크 (버튼에만 다른 링크 걸고 싶을 때)
    private String buttonLinkUrl;

    // 선택: 정렬
    private Integer sortOrder;

    // 선택: 노출여부(Y/N) (없으면 서비스에서 "Y" 기본값 처리 권장)
    private String visibleYn;

    // 선택: 이미지 파일 (FormData key = "image")
    private MultipartFile image;
}