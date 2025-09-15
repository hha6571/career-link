package com.career.careerlink.applicant.resume.entity;

import com.career.careerlink.applicant.resume.entity.enums.EduType;
import com.career.careerlink.applicant.resume.entity.enums.GraduateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Integer educationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Enumerated(EnumType.STRING)
    @Column(name = "edu_type", nullable = false)
    private EduType eduType;

    @Column(name = "school_name", length = 200)
    private String schoolName;

    @Column(name = "exam_name", length = 200)
    private String examName;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "credit_earned", precision = 3, scale = 2)
    private BigDecimal creditEarned;

    @Column(name = "total_credit", precision = 2, scale = 1)
    private BigDecimal totalCredit;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "graduate_status", length = 10)
    private GraduateStatus graduateStatus;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 36)
    private String updatedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}