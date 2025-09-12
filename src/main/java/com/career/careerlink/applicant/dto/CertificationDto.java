package com.career.careerlink.applicant.dto;

import com.career.careerlink.applicant.entity.Certification;
import com.career.careerlink.applicant.entity.Resume;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationDto {
    private Integer certificationId;
    private String name;
    private String issuingOrganization;
    private LocalDate acquiredDate;

    public static CertificationDto of(Certification entity) {
        return CertificationDto.builder()
                .certificationId(entity.getCertificationId())
                .name(entity.getName())
                .issuingOrganization(entity.getIssuingOrganization())
                .acquiredDate(entity.getAcquiredDate())
                .build();
    }

    public static List<CertificationDto> listOf(List<Certification> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(CertificationDto::of).collect(Collectors.toList());
    }

    public Certification toEntity(Resume resume, String createdBy) {
        return Certification.builder()
                .certificationId(this.certificationId)
                .resume(resume)
                .name(this.name)
                .issuingOrganization(this.issuingOrganization)
                .acquiredDate(this.acquiredDate)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    public void updateEntity(Certification entity, String updatedBy) {
        entity.setName(this.name);
        entity.setIssuingOrganization(this.issuingOrganization);
        entity.setAcquiredDate(this.acquiredDate);
        entity.setUpdatedBy(updatedBy);
    }
}
