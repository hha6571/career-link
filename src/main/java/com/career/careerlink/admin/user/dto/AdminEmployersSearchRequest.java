package com.career.careerlink.admin.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminEmployersSearchRequest {
    private Integer page;       // 0-base
    private Integer size;       // page size
    private String sort;        // companyName, email, bizRegNo
    private String direction;   // asc|desc
    private String keyword;     // 통합 검색
}
