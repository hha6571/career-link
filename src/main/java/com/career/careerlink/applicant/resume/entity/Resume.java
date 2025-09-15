package com.career.careerlink.applicant.resume.entity;

import com.career.careerlink.common.enums.YnType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Integer resumeId;

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false, columnDefinition = "enum('Y','N') default 'Y'")
    private YnType isActive;

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

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();
}
