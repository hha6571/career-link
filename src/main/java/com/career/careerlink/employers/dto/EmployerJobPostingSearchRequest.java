package com.career.careerlink.employers.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerJobPostingSearchRequest {
    private Integer page;       // 0-base
    private Integer size;       // page size
    private String sort;        // title, applicationDeadline, isActive, createdAt, createdBy
    private String direction;   // asc|desc
    private String keyword;     // 통합 검색
    private String employerId;
}
