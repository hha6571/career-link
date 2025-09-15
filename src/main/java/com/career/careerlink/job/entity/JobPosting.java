package com.career.careerlink.job.entity;

import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id", updatable = false, nullable = false)
    private Integer jobPostingId;

    @Column(name = "employer_id")
    private String employerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", insertable = false, updatable = false)
    private Employer employer;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "job_field_code")
    private String jobFieldCode;

    @Column(name = "location_code")
    private String locationCode;

    @Column(name = "employment_type_code")
    private String employmentTypeCode;

    @Column(name = "education_level_code")
    private String educationLevelCode;

    @Column(name = "career_level_code")
    private String careerLevelCode;

    @Column(name = "salary_code")
    private String salaryCode;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "is_skillsnap", columnDefinition = "ENUM('Y','N')")
    @Enumerated(EnumType.STRING)
    private AgreementStatus isSkillsnap;

    @Column(name = "is_active", columnDefinition = "ENUM('Y','N')")
    @Enumerated(EnumType.STRING)
    private AgreementStatus isActive;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_deleted", columnDefinition = "ENUM('Y','N')")
    @Enumerated(EnumType.STRING)
    private AgreementStatus isDeleted;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
