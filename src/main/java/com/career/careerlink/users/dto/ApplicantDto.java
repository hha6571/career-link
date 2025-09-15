package com.career.careerlink.users.dto;

import com.career.careerlink.common.enums.YnType;
import com.career.careerlink.users.entity.Applicant;
import com.career.careerlink.users.entity.enums.UserStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDto {
    private String userId;
    private String loginId;
    private String userName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String gender;
    private String userType;
    private String email;

    private LocalDateTime lastLoginAt;
    private LocalDateTime dormantAt;

    private YnType agreeTerms;
    private YnType agreePrivacy;
    private YnType agreeMarketing;
    private UserStatus userStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Entity → DTO 변환
    public static ApplicantDto of(Applicant applicant) {
        return ApplicantDto.builder()
                .userId(applicant.getUserId())
                .loginId(applicant.getLoginId())
                .userName(applicant.getUserName())
                .phoneNumber(applicant.getPhoneNumber())
                .birthDate(applicant.getBirthDate())
                .gender(applicant.getGender())
                .userType(applicant.getUserType())
                .email(applicant.getEmail())
                .lastLoginAt(applicant.getLastLoginAt())
                .dormantAt(applicant.getDormantAt())
                .agreeTerms(applicant.getAgreeTerms())
                .agreePrivacy(applicant.getAgreePrivacy())
                .agreeMarketing(applicant.getAgreeMarketing())
                .userStatus(applicant.getUserStatus())
                .createdAt(applicant.getCreatedAt())
                .updatedAt(applicant.getUpdatedAt())
                .createdBy(applicant.getCreatedBy())
                .updatedBy(applicant.getUpdatedBy())
                .build();
    }

    // DTO → Entity 수정용
    public void updateEntity(Applicant applicant) {
        applicant.setPhoneNumber(this.phoneNumber);
        applicant.setAgreeMarketing(this.agreeMarketing);
        applicant.setGender(this.gender);
        applicant.setUpdatedAt(LocalDateTime.now());
    }
}
