package com.career.careerlink.global.exception;

import com.career.careerlink.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class CareerLinkException extends RuntimeException {
    private final ErrorCode code;

    /** 메시지만 주면 기본적으로 400 INVALID_REQUEST */
    public CareerLinkException(String message) {
        super(message);
        this.code = ErrorCode.INVALID_REQUEST; // 기본 400
    }

    /** 특정 에러코드로도 던질 수 있게 옵션 제공 */
    public CareerLinkException(ErrorCode code, String message) {
        super(message);
        this.code = code != null ? code : ErrorCode.INVALID_REQUEST;
    }
}
