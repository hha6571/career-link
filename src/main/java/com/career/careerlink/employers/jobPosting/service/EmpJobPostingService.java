package com.career.careerlink.employers.jobPosting.service;

import com.career.careerlink.employers.jobPosting.dto.ApplicationDto;
import com.career.careerlink.employers.jobPosting.dto.ApplicationRequestDto;
import com.career.careerlink.employers.jobPosting.dto.JobPostingSimpleDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmpJobPostingService {
    Page<ApplicationDto> getApplications(ApplicationRequestDto req);
    List<JobPostingSimpleDto> getMyJobPostings(String employerUserId);
    boolean updateStatuses(List<ApplicationDto> updates, String employerUserId);
    //Map<String, Object> getApplicationPreview(Integer applicationId, String employerUserId);
}

