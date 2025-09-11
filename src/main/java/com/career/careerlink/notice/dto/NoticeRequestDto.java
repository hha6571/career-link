package com.career.careerlink.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequestDto {
    private Integer page;
    private Integer size;
    private String sort;
    private String direction;
    private String keyword;
    private String noticeType;
}
