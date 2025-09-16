package com.career.careerlink.employers.info.service;

import com.career.careerlink.employers.info.dto.EmployerPublicProfileDto;

public interface EmployerPublicService {
    EmployerPublicProfileDto getPublicProfile(String employerId);
}
