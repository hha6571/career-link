package com.career.careerlink.admin.jobPosting.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminJobPostingSearchRequest {
    private Integer page;       // 0-base
    private Integer size;       // page size
    private String sort;        // companyName, title, applicationDeadline, isActive, createdAt, createdBy
    private String direction;   // asc|desc
    private String keyword;     // 통합 검색
}
