package com.career.careerlink.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
//전체 응답 구조 ( header + body + pagination )
public class ResponseResult {
    private ResponseResultHeader header; // 응답상태 정보(status + message)
    private Object body; // 데이터 정보
    private PaginationInfo pagination; // 선택적 정보
}