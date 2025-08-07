package com.career.careerlink.global.util;

import com.career.careerlink.common.response.PaginationInfo;

public class PaginationUtil {

    public static PaginationInfo from(int page, int size, int totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;

        return PaginationInfo.builder()
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }
}

