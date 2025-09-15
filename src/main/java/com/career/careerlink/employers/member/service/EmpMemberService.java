package com.career.careerlink.employers.member.service;

import com.career.careerlink.employers.member.dto.EmployerMemberDto;
import com.career.careerlink.employers.member.dto.EmployerMemberSearchRequest;
import com.career.careerlink.employers.member.dto.EmployerSignupDto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmpMemberService {
    void empSignup(EmployerSignupDto dto);
    Page<EmployerMemberDto> getEmployerMembers(EmployerMemberSearchRequest req, String employerUserId);
    int approveOne(@NotBlank String targetEmployerUserId, @NotBlank String employerUserId);
    int approveBulk(@NotBlank List<String>targetEmployerUserId, @NotBlank String employerUserId);
}

