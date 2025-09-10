package com.career.careerlink.common.dto;

import lombok.*;

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
