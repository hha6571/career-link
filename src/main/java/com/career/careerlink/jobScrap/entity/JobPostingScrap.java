package com.career.careerlink.jobScrap.entity;

import com.career.careerlink.job.entity.JobPosting;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_posting_scraps",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_scrap_user_posting", columnNames = {"user_id", "job_posting_id"})
        },
        indexes = {
                @Index(name = "idx_scrap_user", columnList = "user_id"),
                @Index(name = "idx_scrap_posting", columnList = "job_posting_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Integer scrapId; //

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false, foreignKey = @ForeignKey(name = "fk_scrap_posting"))
    private JobPosting jobPosting;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, length = 36, updatable = false)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
