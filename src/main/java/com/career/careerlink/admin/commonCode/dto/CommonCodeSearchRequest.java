package com.career.careerlink.admin.commonCode.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommonCodeSearchRequest {
    // DataGrid 기준: 0-based page
    private Integer page;        // 기본 0
    private Integer size;        // 기본 10
    private String sort;         // groupCode | code | codeName | parentCode | sortOrder | level | useYn
    private String direction;    // asc | desc
    //상위코드조회조건
    private String keyword;      // group_code / code / code_name을 한 번에 LIKE
    //하위코드 조회 조건
    private String groupCode;
    private String parentCode;
}
