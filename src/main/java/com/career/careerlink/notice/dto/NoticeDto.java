package com.career.careerlink.notice.dto;

import com.career.careerlink.notice.entity.enums.NoticeType;
import com.career.careerlink.notice.entity.enums.YN;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    private Long noticeId;                // 공지 ID
    private NoticeType noticeType;        // 공지 유형 (GENERAL, SYSTEM, RECRUIT)
    private String title;                 // 제목
    private String writerId;              // 작성자 ID
    private String thumbnailUrl;          // 썸네일 URL
    private String attachmentUrl;         // 첨부파일 URL
    private Integer viewCount;            // 조회수
    private YN isTopFixed;                // 상단 고정 여부
    private YN isExposed;                 // 노출 여부
    private YN isDeleted;                 // 삭제 여부
    private LocalDate startDate;          // 노출 시작일
    private LocalDate endDate;            // 노출 종료일
    private LocalDateTime createdAt;      // 생성일시
    private LocalDateTime updatedAt;      // 수정일시

    private static final String DEFAULT_THUMBNAIL_URL =
            "https://careerlinkbucket.s3.amazonaws.com/notice/907783f0-f673-4ff5-a6e6-66ba84ca9b8d_CareerLink_thumbnail.png";

    public String getThumbnailUrl() {
        return (thumbnailUrl == null || thumbnailUrl.isBlank())
                ? DEFAULT_THUMBNAIL_URL
                : thumbnailUrl;
    }
}
