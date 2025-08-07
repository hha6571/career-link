package com.career.careerlink.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationInfo {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
}
