package com.career.careerlink.employers.info.service;

import com.career.careerlink.employers.info.dto.EmployerInformationDto;
import com.career.careerlink.employers.info.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.info.entiry.Employer;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface EmpInfoService {
    boolean isCompanyDuplicate(String bizRegNo);
    void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file);
    EmployerInformationDto getCompanyInformation();
    Employer saveEmployerInfo(EmployerInformationDto dto, MultipartFile companyLogo);
    void deleteCompanyLogo();
}

