package com.career.careerlink.employers.service;

import com.career.careerlink.employers.dto.EmployerInformationDto;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.dto.EmployerSignupDto;
import com.career.careerlink.employers.entity.Employer;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface EmployerService {
    boolean isCompanyDuplicate(String bizRegNo);
    void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file);
    EmployerInformationDto getCompanyInformation(String employerId);
    Employer saveEmployerInfo(EmployerInformationDto dto, MultipartFile companyLogo);
    void empSignup(EmployerSignupDto dto);
}

