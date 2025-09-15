package com.career.careerlink.employers.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequestDto {
    private Integer jobPostingId;   // 특정 공고 필터 (null이면 전체)
    private String keyword;         // 지원자명, 이메일, 이력서 제목 검색

    private Integer page;           // 페이지 번호 (0-based)
    private Integer size;           // 페이지 크기
    private String sort;            // 정렬 기준 (status, appliedAt 등)
    private String direction;       // asc / desc
}
