package com.career.careerlink.applicant.application.entity;

import com.career.careerlink.applicant.application.entity.enums.ApplicationStatus;
import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import com.career.careerlink.applicant.resume.entity.Resume;
import com.career.careerlink.job.entity.JobPosting;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_letter_id")
    private CoverLetter coverLetter; // optional

    @Column(nullable = false, length = 36)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('SUBMITTED','UNDER_REVIEW','PASSED','FAILED','CANCELLED')")
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(columnDefinition = "json")
    private String resumeSnapshot;

    @Column(columnDefinition = "json")
    private String coverLetterSnapshot;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
