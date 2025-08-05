package com.career.careerlink.employers.service;

import com.career.careerlink.employers.dto.EmployerInfomationDto;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface EmployerService {
    boolean isCompanyDuplicate(String bizRegNo);
    void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file);
    EmployerInfomationDto getCompanyInfomation(String employerId);
    void saveEmployerInfo(EmployerInfomationDto dto);
}

