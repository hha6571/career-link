package com.career.careerlink.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResultHeader {
    private boolean result;
    private String message;
    private String code; //GlobalResponseAdvice 와 GlobalExceptionHandler 정의 하는 httpstatus
}
