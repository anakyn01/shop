package com.hbk.service;

import com.hbk.dto.BannerCreateRequest;
import com.hbk.dto.BannerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {
    List<BannerResponse> list();
    List<BannerResponse> listVisible();
    BannerResponse create(BannerCreateRequest req, MultipartFile image);
}
