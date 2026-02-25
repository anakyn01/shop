package com.hbk.controller;

import com.hbk.dto.*;
import com.hbk.service.FooterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/footer")
public class FooterController {

    private final FooterService footerService;

    // 1차 카테고리
    @GetMapping("/categories")
    public List<FooterCategoryRes> categories() {
        return footerService.categories();
    }

    @PostMapping("/categories")
    public FooterCategoryRes createCategory(@RequestBody FooterCategoryCreateReq req) {
        return footerService.createCategory(req);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable long id) {
        footerService.deleteCategory(id);
    }

    // 2차 링크
    @GetMapping("/categories/{categoryId}/links")
    public List<FooterLinkRes> links(@PathVariable long categoryId) {
        return footerService.linksByCategory(categoryId);
    }

    @PostMapping("/links")
    public FooterLinkRes createLink(@RequestBody FooterLinkCreateReq req) {
        return footerService.createLink(req);
    }

    @DeleteMapping("/links/{id}")
    public void deleteLink(@PathVariable long id) {
        footerService.deleteLink(id);
    }

    // 하단 문구(문단 2개)
    @GetMapping("/text")
    public FooterTextRes getText() {
        return footerService.getText();
    }

    @PutMapping("/text")
    public FooterTextRes upsertText(@RequestBody FooterTextUpsertReq req) {
        return footerService.upsertText(req);
    }
}