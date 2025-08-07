package com.career.careerlink.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceResult {
    private final boolean result;
    private final String message;
    private final ErrorCode errorCode;
    private final SuccessCode successCode;
    private final PaginationInfo pagination;
    private final Object body;

    private ServiceResult(boolean result, String message, ErrorCode errorCode,
                          SuccessCode successCode, PaginationInfo pagination, Object body) {
        this.result = result;
        this.message = message;
        this.errorCode = errorCode;
        this.successCode = successCode;
        this.pagination = pagination;
        this.body = body;
    }

    // ✅ 성공 응답들

    public static ServiceResult success(SuccessCode successCode) {
        return new ServiceResult(true, successCode.getMessage(), null, successCode, null, null);
    }

    public static ServiceResult success(SuccessCode successCode, Object body) {
        return new ServiceResult(true, successCode.getMessage(), null, successCode, null, body);
    }

    public static ServiceResult success(SuccessCode successCode, Object body, PaginationInfo pagination) {
        return new ServiceResult(true, successCode.getMessage(), null, successCode, pagination, body);
    }

    // ✅ 실패 응답들
    public static ServiceResult fail(ErrorCode errorCode) {
        return new ServiceResult(false, errorCode.getMessage(), errorCode, null, null, null);
    }

    public static ServiceResult fail(ErrorCode errorCode, Object body) {
        return new ServiceResult(false, errorCode.getMessage(), errorCode, null, null, body);
    }
}

