package com.career.careerlink.employers.service;

import com.career.careerlink.employers.dto.EmployerInfomationDto;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;

public interface EmployerService {
    boolean isCompanyDuplicate(String bizRegNo);
    void companyRegistrationRequest(EmployerRegistrationDto dto);
    EmployerInfomationDto getCompanyInfomation(String employerId);
}

