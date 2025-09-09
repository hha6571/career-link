package com.career.careerlink.employers.service;

import com.career.careerlink.employers.dto.*;
import com.career.careerlink.employers.entity.Employer;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployerService {
    boolean isCompanyDuplicate(String bizRegNo);
    void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file);
    void empSignup(EmployerSignupDto dto);
    EmployerInformationDto getCompanyInformation();
    Employer saveEmployerInfo(EmployerInformationDto dto, MultipartFile companyLogo);
    void deleteCompanyLogo();
    Page<EmployerMemberDto> getEmployerMembers(EmployerMemberSearchRequest req, String employerUserId);
    int approveOne(@NotBlank String targetEmployerUserId, @NotBlank String employerUserId);
    int approveBulk(@NotBlank List<String>targetEmployerUserId, @NotBlank String employerUserId);
}

