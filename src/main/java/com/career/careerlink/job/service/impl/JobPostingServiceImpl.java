package com.career.careerlink.job.service.impl;

import com.career.careerlink.admin.jobPosting.dto.AdminJobPostingResponse;
import com.career.careerlink.admin.jobPosting.dto.AdminJobPostingSearchRequest;
import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.common.service.CommonService;
import com.career.careerlink.employers.jobPosting.dto.EmployerJobPostingResponse;
import com.career.careerlink.employers.jobPosting.dto.EmployerJobPostingSearchRequest;
import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.users.entity.EmployerUsers;
import com.career.careerlink.employers.info.repository.EmployerRepository;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.job.dto.*;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.job.mapper.JobPostingMapper;
import com.career.careerlink.job.repository.JobRepository;
import com.career.careerlink.job.service.JobPostingService;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.career.careerlink.job.spec.JobPostingSpecs.*;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final EmployerUserRepository employerUserRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final CommonService commonCodeService;
    private final JobPostingMapper jobPostingMapper;

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
                .jobField(e.getJobFieldCode())
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
    @Transactional
    public JobPostingResponse detailJobPosting(int jobPostingId) {
        jobRepository.incrementViewCount(jobPostingId);

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

    @Override
    public Page<EmployerJobPostingResponse> searchForEmployer(EmployerJobPostingSearchRequest req, String employerUserId) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        // 정렬 방향 보정
        String direction = Optional.ofNullable(req.getDirection())
                .orElse("asc")
                .toUpperCase(Locale.ROOT);
        direction = "DESC".equals(direction) ? "DESC" : "ASC";

        // 정렬 필드 보정(화이트리스트 키)
        String sort = Optional.ofNullable(req.getSort()).orElse("jobPostingId");

        long total = jobPostingMapper.searchForEmployerCount(req, employerUserId);
        List<EmployerJobPostingResponse> rows = jobPostingMapper.searchForEmployer(req, employerUserId, offset, safeSize, sort, direction);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public int deleteBulkByEmployer(List<String> targetJobPostingIds, String employerUserId){
        return jobPostingMapper.deleteBulkByEmployer(
                targetJobPostingIds,
                employerUserId
        );
    }

    @Override
    public Page<AdminJobPostingResponse> searchForAdmin(AdminJobPostingSearchRequest req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        // 정렬 방향 보정
        String direction = Optional.ofNullable(req.getDirection())
                .orElse("asc")
                .toUpperCase(Locale.ROOT);
        direction = "DESC".equals(direction) ? "DESC" : "ASC";

        // 정렬 필드 보정(화이트리스트 키)
        String sort = Optional.ofNullable(req.getSort()).orElse("jobPostingId");

        long total = jobPostingMapper.searchForAdminCount(req);
        List<AdminJobPostingResponse> rows = jobPostingMapper.searchForAdmin(req, offset, safeSize, sort, direction);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public int deleteBulkByAdmin(List<String> targetJobPostingIds){
        return jobPostingMapper.deleteBulkByAdmin(
                targetJobPostingIds
        );
    }

    private record HotCursor(int view, int id) {}

    private HotCursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        try {
            if (cursor.contains("_")) {
                String[] p = cursor.split("_");
                return new HotCursor(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
            }
            String raw = new String(Base64.getUrlDecoder().decode(cursor));
            String[] p = raw.split("_");
            return new HotCursor(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
        } catch (Exception e) { return null; }
    }


    private String encode(HotCursor c) {
        String raw = c.view + "_" + c.id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
    }


    public HotDtos.HotResponse getHot(HotDtos.HotRequest req) {
        int limit = (req.limit() == null || req.limit() <= 0) ? 16 : Math.min(req.limit(), 50);
        var c = decode(req.cursor());


        List<Map<String,Object>> rows = jobPostingMapper.selectHotJobs(
                limit,
                c != null ? c.view : null,
                c != null ? c.id : null
        );


        var items = new ArrayList<HotDtos.HotItem>(rows.size());
        for (var r : rows) items.add(HotDtos.HotItem.fromMap(r));


        String nextCursor = null;
        boolean hasMore = false;
        if (!items.isEmpty()) {
            var last = items.get(items.size()-1);
            nextCursor = encode(new HotCursor(last.viewCount(), last.jobId()));
            hasMore = true;
        }
        return new HotDtos.HotResponse(items, nextCursor, hasMore);
    }
}
