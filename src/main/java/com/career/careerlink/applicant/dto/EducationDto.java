package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.Education;
import com.career.careerlink.applicant.entity.Resume;
import com.career.careerlink.applicant.entity.enums.EduType;
import com.career.careerlink.applicant.entity.enums.GraduateStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDto {
    private Integer educationId;
    private EduType eduType;
    private String schoolName;
    private String examName;
    private LocalDate examDate;
    private String major;
    private BigDecimal creditEarned;
    private BigDecimal totalCredit;
    private LocalDate startDate;
    private LocalDate endDate;
    private GraduateStatus graduateStatus;

    public static EducationDto of(Education entity) {
        return EducationDto.builder()
                .educationId(entity.getEducationId())
                .eduType(entity.getEduType())
                .schoolName(entity.getSchoolName())
                .examName(entity.getExamName())
                .examDate(entity.getExamDate())
                .major(entity.getMajor())
                .creditEarned(entity.getCreditEarned())
                .totalCredit(entity.getTotalCredit())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .graduateStatus(entity.getGraduateStatus())
                .build();
    }

    public static List<EducationDto> listOf(List<Education> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(EducationDto::of).collect(Collectors.toList());
    }

    public Education toEntity(Resume resume, String createdBy) {
        return Education.builder()
                .educationId(this.educationId)
                .resume(resume)
                .eduType(this.eduType)
                .schoolName(this.schoolName)
                .examName(this.examName)
                .examDate(this.examDate)
                .major(this.major)
                .creditEarned(this.creditEarned)
                .totalCredit(this.totalCredit)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .graduateStatus(this.graduateStatus)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    public void updateEntity(Education entity, String updatedBy) {
        entity.setEduType(this.eduType);
        entity.setSchoolName(this.schoolName);
        entity.setExamName(this.examName);
        entity.setExamDate(this.examDate);
        entity.setMajor(this.major);
        entity.setCreditEarned(this.creditEarned);
        entity.setTotalCredit(this.totalCredit);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setGraduateStatus(this.graduateStatus);
        entity.setUpdatedBy(updatedBy);
    }
}
