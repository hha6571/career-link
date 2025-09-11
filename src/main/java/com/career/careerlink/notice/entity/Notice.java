package com.career.careerlink.notice.entity;

import com.career.careerlink.notice.entity.enums.NoticeType;
import com.career.careerlink.notice.entity.enums.YN;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type", nullable = false, length = 50)
    private NoticeType noticeType;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content; // Quill HTML

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "writer_id", nullable = false, columnDefinition = "varchar(50) default 'CareerLink'")
    private String writerId = "CareerLink";

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_top_fixed", nullable = false, length = 1)
    private YN isTopFixed = YN.N;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_exposed", nullable = false, length = 1)
    private YN isExposed = YN.Y;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false, length = 1)
    private YN isDeleted = YN.N;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void softDelete() {
        if (YN.N.equals(this.isDeleted)) {
            this.isDeleted = YN.Y;
        }
    }

    public void increaseViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}
