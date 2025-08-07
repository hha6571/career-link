package com.career.careerlink.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseResultHeader {
    private boolean result;
    private String message;
}
