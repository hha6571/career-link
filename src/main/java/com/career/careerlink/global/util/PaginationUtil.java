package com.career.careerlink.global.util;

import com.career.careerlink.common.response.PaginationInfo;

public final class PaginationUtil {
    private PaginationUtil() {}

    public static PaginationInfo of(int page /*0-based*/, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / Math.max(size, 1));
        boolean hasPrevious = page > 0;
        boolean hasNext = page + 1 < totalPages;

        return PaginationInfo.builder()
                .page(page)               // 0-based로 그대로
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .hasPrevious(hasPrevious)
                .hasNext(hasNext)
                .build();
    }
}


