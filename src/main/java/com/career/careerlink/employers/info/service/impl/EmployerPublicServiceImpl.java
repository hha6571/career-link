package com.career.careerlink.employers.info.service.impl;


import com.career.careerlink.common.enums.YnType;
import com.career.careerlink.employers.info.dto.EmployerPublicProfileDto;
import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.employers.info.repository.EmployerRepository;
import com.career.careerlink.employers.info.service.EmployerPublicService;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EmployerPublicServiceImpl implements EmployerPublicService {

    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    @Override
    public EmployerPublicProfileDto getPublicProfile(String employerId) {
        Employer emp = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "기업을 찾을 수 없습니다."));

        long activePostingCount = jobRepository
                .countByEmployerIdAndIsDeletedAndIsActive(employerId, YnType.N, YnType.Y);

        List<JobPosting> recents = jobRepository
                .findByEmployerIdAndIsDeletedAndIsActiveOrderByCreatedAtDesc(
                        employerId, YnType.N, YnType.Y, PageRequest.of(0, 6));

        List<EmployerPublicProfileDto.JobPostingSummaryDto> recentDtos = recents.stream()
                .map(jp -> EmployerPublicProfileDto.JobPostingSummaryDto.builder()
                        .jobPostingId(Long.valueOf(jp.getJobPostingId()))
                        .title(jp.getTitle())
                        .jobFieldCode(jp.getJobFieldCode())
                        .jobFieldName(jp.getJobFieldCode())
                        .locationCode(jp.getLocationCode())
                        .locationName(jp.getLocationCode())
                        .employmentTypeCode(jp.getEmploymentTypeCode())
                        .employmentTypeName(jp.getEmploymentTypeCode())
                        .careerLevelCode(jp.getCareerLevelCode())
                        .careerLevelName(jp.getCareerLevelCode())
                        .salaryCode(jp.getSalaryCode())
                        .salaryName(jp.getSalaryCode())
                        .applicationDeadline(jp.getApplicationDeadline())
                        .viewCount(jp.getViewCount() == null ? 0 : jp.getViewCount())
                        .build())
                .toList();

        return EmployerPublicProfileDto.builder()
                .employerId(emp.getEmployerId())
                .companyName(emp.getCompanyName())
                .companyLogoUrl(emp.getCompanyLogoUrl())
                .homepageUrl(emp.getHomepageUrl())
                .companyIntro(emp.getCompanyIntro())
                .locationCode(emp.getBaseAddress())
                .activePostingCount(activePostingCount)
                .hiring(activePostingCount > 0)
                .recentPostings(recentDtos)
                .build();
    }
}