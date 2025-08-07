package com.career.careerlink.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    OK("조회되었습니다."),
    CREATED("생성되었습니다."),
    UPDATED("수정되었습니다."),
    DELETED("삭제되었습니다.");

    private final String message;
}
