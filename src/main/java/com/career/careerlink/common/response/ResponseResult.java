package com.career.careerlink.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {
    private ResponseResultHeader header; // 상태/메시지/코드
    private T body;                      // 데이터
    private PaginationInfo pagination;   // DataGrid 페이징(선택)
}
