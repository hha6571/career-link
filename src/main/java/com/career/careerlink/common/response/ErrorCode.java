package com.career.careerlink.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNKNOWN_ERROR(HttpStatus.NOT_FOUND, "찾을 수 없습니다."),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 데이터를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 데이터입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ACCOUNT_DORMANT(HttpStatus.LOCKED, "휴면계정입니다. 본인확인 후 해제해주세요."),
    ACCOUNT_DELETED(HttpStatus.GONE, "탈퇴 처리된 계정입니다."),
    EMPLOYER_NOT_APPROVED(HttpStatus.FORBIDDEN, "기업회원 승인 대기 상태입니다."),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "로그인 실패가 여러 번 발생했습니다. 잠시 후 다시 시도하세요.");

    private final HttpStatus status;
    private final String message;
}
