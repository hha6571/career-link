package com.career.careerlink.applicant.application.service;

import com.career.careerlink.applicant.application.dto.ApplicationResponseDto;
import com.career.careerlink.applicant.application.dto.ApplicationRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    ApplicationResponseDto apply(ApplicationRequestDto requestDto);
    Page<ApplicationResponseDto> getMyApplications(String period, Pageable pageable);
    ApplicationResponseDto cancelApplication(Integer applicationId);
    ApplicationResponseDto reapply(Integer applicationId);
}
