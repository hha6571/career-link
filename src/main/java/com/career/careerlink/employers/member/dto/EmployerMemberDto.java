package com.career.careerlink.employers.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerMemberDto {
    private String employerUserId;
    private String employerId;
    private String userName;
    private String employerLoginId;
    private String email;
    private String phoneNumber;
    private String isApproved; // "Y" | "N"
    private String approvedAt;
}