package com.career.careerlink.notice.dto;

import com.career.careerlink.notice.entity.Notice;
import com.career.careerlink.notice.entity.enums.NoticeType;
import com.career.careerlink.notice.entity.enums.YN;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDetailDto {
    private Integer noticeId;              // 공지 ID
    private NoticeType noticeType;      // 공지 유형
    private String title;               // 제목
    private String content;             // 공지 내용 (Quill HTML)
    private String fileUrl;             // 첨부파일 URL
    private String writerId;            // 작성자 ID
    private Integer viewCount;          // 조회수
    private YN isTopFixed;              // 상단 고정 여부
    private YN isExposed;               // 노출 여부
    private YN isDeleted;               // 삭제 여부
    private LocalDate startDate;        // 노출 시작일
    private LocalDate endDate;          // 노출 종료일
    private String createdBy;           // 등록자
    private String updatedBy;           // 수정자
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시

    // DTO → Entity (신규 등록용)
    public Notice toEntity(String fileUrl) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return Notice.builder()
                .noticeType(this.noticeType)
                .title(this.title)
                .content(this.content)
                .fileUrl(fileUrl)
                .viewCount(0)
                .isTopFixed(this.isTopFixed)
                .isExposed(this.isExposed)
                .isDeleted(YN.N)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .createdBy(userName)
                .updatedBy(userName)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // DTO → Entity 갱신 (수정용)
    public void updateEntity(Notice notice, String fileUrl) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        notice.setNoticeType(this.noticeType);
        notice.setTitle(this.title);
        notice.setContent(this.content);
        notice.setFileUrl(fileUrl);
        notice.setStartDate(this.startDate);
        notice.setEndDate(this.endDate);
        notice.setIsTopFixed(this.isTopFixed);
        notice.setIsExposed(this.isExposed);
        notice.setUpdatedBy(userName);
        notice.setUpdatedAt(LocalDateTime.now());
    }

    // Entity → DTO
    public static NoticeDetailDto of(Notice notice) {
        return NoticeDetailDto.builder()
                .noticeId(notice.getNoticeId())
                .noticeType(notice.getNoticeType())
                .title(notice.getTitle())
                .content(notice.getContent())
                .fileUrl(notice.getFileUrl())
                .viewCount(notice.getViewCount())
                .isTopFixed(notice.getIsTopFixed())
                .isExposed(notice.getIsExposed())
                .isDeleted(notice.getIsDeleted())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .createdBy(notice.getCreatedBy())
                .updatedBy(notice.getUpdatedBy())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
