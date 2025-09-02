package com.career.careerlink.employers.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerMemberSearchRequest {
    private Integer page;       // 0-base
    private Integer size;       // page size
    private String sort;        // name, loginId, email, phone, isApproved, joinedAt
    private String direction;   // asc|desc
    private String keyword;     // 통합 검색
}
