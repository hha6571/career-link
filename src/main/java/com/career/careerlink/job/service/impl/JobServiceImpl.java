package com.career.careerlink.job.service.impl;

import com.career.careerlink.admin.dto.CommonCodeDto;
import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.common.service.CommonService;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.entity.EmployerUsers;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.employers.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.job.dto.*;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.job.repository.JobRepository;
import com.career.careerlink.job.service.JobService;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.career.careerlink.job.spec.JobPostingSpecs.*;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final EmployerUserRepository employerUserRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final CommonService commonCodeService;

    private static final String G_JOB_FIELD = "JOB_FIELD";
    private static final String G_LOCATION  = "LOCATION";
    private static final String G_EMP_TYPE  = "EMPLOYMENT_TYPE";
    private static final String G_EDUCATION = "EDUCATION_LEVEL";
    private static final String G_CAREER    = "CAREER_LEVEL";
    private static final String G_SALARY    = "SALARY";

    @Transactional
    @Override
    public JobPostingResponse saveJobPosting(String employerUserId, CreateJobPostingRequest dto) {
        // 1. employer_user 검증
        EmployerUsers employerUser = employerUserRepository.findByEmployerUserId(employerUserId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String employerId = employerUser.getEmployerId();

        // 2. employer 검증
        Employer employer = employerRepository.findByEmployerId(employerId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "기업 정보를 찾을 수 없습니다."));

        if (!"Y".equalsIgnoreCase(String.valueOf(employerUser.getIsApproved()))) {
            throw new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "승인되지 않은 기업은 공고 등록이 불가합니다.");
        }

        JobPosting posting = new JobPosting();
        posting.setEmployerId(employerUser.getEmployerId());
        posting.setTitle(dto.getTitle());
        posting.setDescription(dto.getDescription());
        posting.setJobFieldCode(dto.getJobFieldCode());
        posting.setLocationCode(dto.getLocationCode());
        posting.setEmploymentTypeCode(dto.getEmploymentTypeCode());
        posting.setEducationLevelCode(dto.getEducationLevelCode());
        posting.setCareerLevelCode(dto.getCareerLevelCode());
        posting.setSalaryCode(dto.getSalaryCode());
        posting.setApplicationDeadline(dto.getApplicationDeadline());
        posting.setIsActive(dto.getIsActive());
        posting.setCreatedBy(employerUserId);
        posting.setUpdatedBy(employerUserId);
        posting.setIsDeleted(AgreementStatus.N);

        JobPosting saved = jobRepository.save(posting);
        return JobPostingResponse.from(saved);
    }

    @Override
    public JobFiltersResponse getFilters() {
        var jobFields       = commonCodeService.allCodesByGroup(G_JOB_FIELD);
        var locations       = commonCodeService.allCodesByGroup(G_LOCATION);
        var employmentTypes = commonCodeService.allCodesByGroup(G_EMP_TYPE);
        var educationLevels = commonCodeService.allCodesByGroup(G_EDUCATION);
        var careerLevels    = commonCodeService.allCodesByGroup(G_CAREER);
        var salary          = commonCodeService.allCodesByGroup(G_SALARY);

        return JobFiltersResponse.builder()
                .jobFields(jobFields)
                .locations(locations)
                .employmentTypes(employmentTypes)
                .educationLevels(educationLevels)
                .careerLevels(careerLevels)
                .salary(salary)
                .build();
    }

    private List<CommonCodeDto> safeAll(String group) {
        try {
            return commonCodeService.allCodesByGroup(group);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Page<JobCardResponse> getJobList(JobSearchCond c, Pageable pageable) {
        Specification<JobPosting> spec = Specification.allOf(
                isActive(),
                isDeleted(),
                keywordLike(c.getKeyword()),
                jobFieldIn(c.getJobField()),
                locationIn(c.getLocation()),
                empTypeIn(c.getEmpType()),
                educationIn(c.getEdu()),
                careerLevelIn(c.getExp()),
                salaryIn(c.getSal())
        );

        var page = jobRepository.findAll(spec, pageable);
        return page.map(this::toCard);
    }

    private JobCardResponse toCard(JobPosting e) {
        return JobCardResponse.builder()
                .jobId(e.getJobPostingId())
                .title(e.getTitle())
                .employerId(e.getEmployerId())
                .companyName(e.getEmployer() != null ? e.getEmployer().getCompanyName() : null)
                .companyLogoUrl(e.getEmployer() != null ? e.getEmployer().getCompanyLogoUrl() : null)
                .location(e.getLocationCode())
                .employmentType(e.getEmploymentTypeCode())
                .careerLevel(e.getCareerLevelCode())
                .educationLevel(e.getEducationLevelCode())
                .salary(e.getSalaryCode())
                .postedAt(e.getCreatedAt())
                .deadline(e.getApplicationDeadline())
                .isActive(e.getIsActive())
                .isDeleted(e.getIsDeleted())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostingResponse detailJobPosting(int jobPostingId) {
        return jobRepository.findDetailJobPosting(jobPostingId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "해당 공고를 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void updateJobPosting(Integer jobPostingId, UpdateJobPostingRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isEmp = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMP"));

        JobPosting posting = jobRepository.findById(jobPostingId)
                .orElseThrow(() -> new CareerLinkException(ErrorCode.DATA_NOT_FOUND, "채용공고가 존재하지 않습니다. id=" + jobPostingId));

        if (!isAdmin) {
            if (!isEmp) {
                throw new CareerLinkException(ErrorCode.FORBIDDEN, "해당 채용공고에 대한 수정 권한이 없습니다.");
            }
        }

        posting.setTitle(req.getTitle());
        posting.setDescription(req.getDescription());
        posting.setJobFieldCode(req.getJobFieldCode());
        posting.setLocationCode(req.getLocationCode());
        posting.setEmploymentTypeCode(req.getEmploymentTypeCode());
        posting.setEducationLevelCode(req.getEducationLevelCode());
        posting.setCareerLevelCode(req.getCareerLevelCode());
        posting.setSalaryCode(req.getSalaryCode());
        posting.setApplicationDeadline(req.getApplicationDeadline());
        posting.setIsSkillsnap(req.getIsSkillsnap());
        posting.setIsActive(req.getIsActive());
        posting.setUpdatedBy(auth.getName());
        posting.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(posting);
    }
}
