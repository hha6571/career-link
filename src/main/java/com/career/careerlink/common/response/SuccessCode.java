package com.career.careerlink.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    OK(HttpStatus.OK, "조회되었습니다."),
    CREATED(HttpStatus.CREATED, "생성되었습니다."),
    UPDATED(HttpStatus.OK, "수정되었습니다."),
    DELETED(HttpStatus.NO_CONTENT, "삭제되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "내용이 없습니다."); // ★ 추가

    private final HttpStatus status;
    private final String message;
}
