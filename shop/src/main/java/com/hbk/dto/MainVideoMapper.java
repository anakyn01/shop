package com.hbk.dto;

import com.hbk.entity.MainVideo;

public class MainVideoMapper {

    public static MainVideoResponse toResponse(MainVideo v) {
        if (v == null) return null;

        return MainVideoResponse.builder()
                .id(v.getId())
                .videoUrl(v.getVideoUrl())
                .title(v.getTitle())
                .subtitle(v.getSubtitle())
                .btn1Text(v.getBtn1Text())
                .btn1Link(v.getBtn1Link())
                .btn2Text(v.getBtn2Text())
                .btn2Link(v.getBtn2Link())
                .build();
    }

    /** ✅ 텍스트 필드만 엔티티에 반영 (videoUrl은 서비스에서 파일 저장 후 세팅) */
    public static void apply(MainVideo target, MainVideoRequest req) {
        if (target == null || req == null) return;

        target.setTitle(trimToNull(req.getTitle()) == null ? "" : req.getTitle().trim());
        target.setSubtitle(trimToNull(req.getSubtitle()) == null ? "" : req.getSubtitle().trim());

        target.setBtn1Text(trimToNull(req.getBtn1Text()));
        target.setBtn1Link(trimToNull(req.getBtn1Link()));
        target.setBtn2Text(trimToNull(req.getBtn2Text()));
        target.setBtn2Link(trimToNull(req.getBtn2Link()));
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}