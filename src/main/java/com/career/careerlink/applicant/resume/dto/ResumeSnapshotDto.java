package com.career.careerlink.applicant.resume.dto;

import com.career.careerlink.applicant.resume.entity.Resume;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeSnapshotDto {
    private Integer resumeId;
    private String title;

    private List<EducationDto> educations;
    private List<ExperienceDto> experiences;
    private List<CertificationDto> certifications;
    private List<SkillDto> skills;

    /** Entity → DTO 변환 */
    public static ResumeSnapshotDto of(Resume entity) {
        if (entity == null) return null;

        return ResumeSnapshotDto.builder()
                .resumeId(entity.getResumeId())
                .title(entity.getTitle())
                .educations(entity.getEducations() != null
                        ? entity.getEducations().stream()
                        .map(EducationDto::of)
                        .collect(Collectors.toList())
                        : null)
                .experiences(entity.getExperiences() != null
                        ? entity.getExperiences().stream()
                        .map(ExperienceDto::of)
                        .collect(Collectors.toList())
                        : null)
                .certifications(entity.getCertifications() != null
                        ? entity.getCertifications().stream()
                        .map(CertificationDto::of)
                        .collect(Collectors.toList())
                        : null)
                .skills(entity.getSkills() != null
                        ? entity.getSkills().stream()
                        .map(SkillDto::of)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
