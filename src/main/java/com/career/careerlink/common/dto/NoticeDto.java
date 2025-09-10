package com.career.careerlink.common.dto;

import com.career.careerlink.common.entity.enums.NoticeType;
import com.career.careerlink.common.entity.enums.YN;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    private Long noticeId;                    // 공지 ID
    private NoticeType noticeType;      // 공지 유형 (GENERAL, SYSTEM, RECRUIT)
    private String title;               // 제목
    private String writerId;            // 작성자 ID
    private Integer viewCount;          // 조회수
    private YN isTopFixed;              // 상단 고정 여부
    private YN isExposed;               // 노출 여부
    private YN isDeleted;               // 삭제 여부
    private LocalDate startDate;        // 노출 시작일
    private LocalDate endDate;          // 노출 종료일
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시
}
