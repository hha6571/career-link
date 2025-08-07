package com.career.careerlink.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNKNOWN_ERROR("찾을 수 없습니다."),
    DATA_NOT_FOUND("요청한 데이터를 찾을 수 없습니다."),
    INVALID_REQUEST("잘못된 요청입니다."), // 400 Bad Request
    SYSTEM_ERROR("시스템 오류가 발생했습니다."),
    UNAUTHORIZED("인증이 필요합니다."), // 401 Unauthorized ( 로그인 안 했거나 토큰이 없거나 만료되었을 때 )
    DUPLICATE_RESOURCE("이미 존재하는 데이터입니다."),
    FORBIDDEN("접근 권한이 없습니다.");//403 Forbidden ( 로그인은 했지만 접근 권한이 없을 때 )
    private final String message;
}
