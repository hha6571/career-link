package com.career.careerlink.applicant.resume.dto;

import com.career.careerlink.applicant.resume.entity.Resume;
import com.career.careerlink.common.enums.YnType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeDto {
    private Integer resumeId;
    private String userId;
    private String title;
    private YnType isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<EducationDto> educations;
    private List<ExperienceDto> experiences;
    private List<CertificationDto> certifications;
    private List<SkillDto> skills;

    public static ResumeDto of(Resume entity) {
        return ResumeDto.builder()
                .resumeId(entity.getResumeId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .educations(EducationDto.listOf(entity.getEducations()))
                .experiences(ExperienceDto.listOf(entity.getExperiences()))
                .certifications(CertificationDto.listOf(entity.getCertifications()))
                .skills(SkillDto.listOf(entity.getSkills()))
                .build();
    }

    public static List<ResumeDto> listOf(List<Resume> entities) {
        return entities.stream().map(ResumeDto::of).collect(Collectors.toList());
    }

    public Resume toEntity(String createdBy) {
        return Resume.builder()
                .resumeId(this.resumeId)
                .userId(this.userId)
                .title(this.title)
                .isActive(this.isActive)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    public void updateEntity(Resume entity, String updatedBy) {
        entity.setTitle(this.title);
        entity.setIsActive(this.isActive);
        entity.setUpdatedBy(updatedBy);
    }
}
