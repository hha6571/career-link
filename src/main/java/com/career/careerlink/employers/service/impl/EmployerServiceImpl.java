package com.career.careerlink.employers.service.impl;

import com.career.careerlink.applicant.mapper.CoverLetterMapper;
import com.career.careerlink.applicant.mapper.ResumeMapper;
import com.career.careerlink.employers.dto.*;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.entity.EmployerUsers;
import com.career.careerlink.employers.mapper.ApplicationMapper;
import com.career.careerlink.employers.mapper.EmployerMemberMapper;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.employers.repository.EmployerUserRepository;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.global.s3.S3Service;
import com.career.careerlink.global.s3.S3UploadType;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final EmployerUserRepository employerUserRepository;
    private final S3Service s3Service;
    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;
    private final EmployerMemberMapper employerMemberMapper;
    private final ApplicationMapper applicationMapper;
    private final ResumeMapper resumeMapper;
    private final CoverLetterMapper coverLetterMapper;

    // ----------------------------
    // 공통: 로그인 사용자 → employerId 해석
    // ----------------------------
    private String resolveEmployerIdOfLoggedInUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String employerUserId;
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            employerUserId = ud.getUsername(); // username = employerUserId
        } else {
            employerUserId = String.valueOf(principal);
        }

        EmployerUsers eu = employerUserRepository.findByEmployerUserIdAndIsApproved(employerUserId, AgreementStatus.Y)
                .orElseThrow(() -> new RuntimeException("소속 기업을 찾을 수 없습니다."));

        return eu.getEmployerId();
    }

    /**
     * 기업중복방지 체크
     * @param bizRegNo
     */
    @Override
    public boolean isCompanyDuplicate(String bizRegNo) {
        return employerRepository.existsByBizRegNo(bizRegNo);
    }

    /**
     * 기업등록요청
     * @param dto
     * @param file
     */
    @Override
    public void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file) {
        String generatedUserId = UserIdGenerator.generate("EMP");
        String url = s3Service.uploadFile(S3UploadType.BUSINESS_CERTIFICATE, file);

        Employer newEmployers = Employer.builder()
                .employerId(generatedUserId)
                .companyName(dto.getCompanyName())
                .bizRegNo(dto.getBizRegNo())
                .bizRegistrationUrl(url)
                .ceoName(dto.getCeoName())
                .companyEmail(dto.getCompanyEmail())
                .establishedDate(dto.getEstablishedDate())
                .isApproved(dto.getIsApproved())
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        employerRepository.save(newEmployers);
    }

    /**
     * 기업회원가입
     * @param dto
     */
    @Override
    @Transactional
    public void empSignup(EmployerSignupDto dto) {
        Objects.requireNonNull(dto.getEmployerId(), "employerId is required");
        Objects.requireNonNull(dto.getEmployerLoginId(), "employerLoginId is required");
        Objects.requireNonNull(dto.getPasswordHash(), "password is required");

        String encodedPassword = passwordEncoder.encode(dto.getPasswordHash());

        em.createQuery("select e from Employer e where e.employerId = :id")
                .setParameter("id", dto.getEmployerId())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult();

        long count = employerUserRepository.countByEmployerId(dto.getEmployerId());
        String role = (count == 0) ? "ADMIN" : "EMPLOYEE";

        String generatedUserId = UserIdGenerator.generate("EMU");

        // 승인여부 결정: 첫 가입자(ADMIN)는 무조건 Y
        AgreementStatus approvedStatus = (count == 0)
                ? AgreementStatus.Y
                : dto.getIsApproved();

        EmployerUsers user = EmployerUsers.builder()
                .employerUserId(generatedUserId)
                .employerId(dto.getEmployerId())
                .employerLoginId(dto.getEmployerLoginId())
                .passwordHash(encodedPassword)
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .birthDate(dto.getBirthDate())
                .email(dto.getEmail())
                .role(role)
                .deptName(dto.getDeptName())
                .isApproved(approvedStatus)
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .employerStatus(dto.getEmployerStatus())
                .build();

        employerUserRepository.save(user);
    }

    /**
     * 기업정보 조회
     */
    @Override
    public EmployerInformationDto getCompanyInformation() {
        String employerId = resolveEmployerIdOfLoggedInUser(); // ← 로그인 사용자 기준
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("기업을 찾을 수 없습니다."));

        return toDto(employer);
    }

    /**
     * 기업정보저장
     * @param dto
     * @param companyLogo
     */
    @Override
    public Employer saveEmployerInfo(EmployerInformationDto dto, MultipartFile companyLogo) {
        String employerId = resolveEmployerIdOfLoggedInUser();
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다. id=" + employerId));

        // (선택) 변경 불가 필드는 건드리지 않음: bizRegNo, ceoName, establishedDate 등
        employer.setCompanyTypeCode(dto.getCompanyTypeCode());
        employer.setCompanyName(dto.getCompanyName());
        employer.setCompanyPhone(dto.getCompanyPhone());
        employer.setCompanyEmail(dto.getCompanyEmail());
        employer.setBaseAddress(dto.getBaseAddress());
        employer.setDetailAddress(dto.getDetailAddress());
        employer.setPostcode(dto.getPostcode());
        employer.setIndustryCode(dto.getIndustryCode());
        employer.setCompanyIntro(dto.getCompanyIntro());
        employer.setHomepageUrl(dto.getHomepageUrl());
        employer.setEmployeeCount(dto.getEmployeeCount());

        // 로고 업로드: 새로 업로드되면 교체
        if (companyLogo != null && !companyLogo.isEmpty()) {
            String oldUrl = employer.getCompanyLogoUrl();
            String newUrl = s3Service.uploadFile(S3UploadType.COMPANY_LOGO, companyLogo);
            employer.setCompanyLogoUrl(newUrl);
            if (oldUrl != null) {
                s3Service.deleteFileByUrl(oldUrl);
            }
        }

        return employerRepository.save(employer);
    }

    /**
     * 기업 로고 삭제(로고사진 삭제시만)
     */
    public void deleteCompanyLogo() {
        String employerId = resolveEmployerIdOfLoggedInUser();
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("승인된 소속 기업을 찾을 수 없습니다."));

        if (employer.getCompanyLogoUrl() != null) {
            s3Service.deleteFileByUrl(employer.getCompanyLogoUrl());
            employer.setCompanyLogoUrl(null);
            employerRepository.save(employer);
        }
    }

    private EmployerInformationDto toDto(Employer e) {
        return EmployerInformationDto.builder()
                .employerId(e.getEmployerId())
                .companyTypeCode(e.getCompanyTypeCode())
                .companyName(e.getCompanyName())
                .bizRegNo(e.getBizRegNo())
                .bizRegistrationUrl(e.getBizRegistrationUrl())
                .ceoName(e.getCeoName())
                .companyPhone(e.getCompanyPhone())
                .companyEmail(e.getCompanyEmail())
                .baseAddress(e.getBaseAddress())
                .detailAddress(e.getDetailAddress())
                .postcode(e.getPostcode())
                .establishedDate(e.getEstablishedDate())
                .industryCode(e.getIndustryCode())
                .companyIntro(e.getCompanyIntro())
                .homepageUrl(e.getHomepageUrl())
                .companyLogoUrl(e.getCompanyLogoUrl())
                .employeeCount(e.getEmployeeCount())
                .build();
    }

    @Override
    public Page<EmployerMemberDto> getEmployerMembers(EmployerMemberSearchRequest req, String employerUserId) {
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
        String sort = Optional.ofNullable(req.getSort()).orElse("employerUserId");

        long total = employerMemberMapper.membersCount(req, employerUserId);
        List<EmployerMemberDto> rows = employerMemberMapper.members(req, employerUserId, offset, safeSize, sort, direction);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public int approveOne(String targetEmployerUserId, String employerUserId){
        return employerMemberMapper.approveIfPending(
                targetEmployerUserId,
                employerUserId
        );
    }

    @Override
    @Transactional
    public int approveBulk(List<String> targetEmployerUserIds, String employerUserId){
        return employerMemberMapper.approveIfPendingBulk(
                targetEmployerUserIds,
                employerUserId
        );
    }

    private String resolveEmployerId(String employerUserId) {
        EmployerUsers eu = employerUserRepository.findByEmployerUserIdAndIsApproved(employerUserId, AgreementStatus.Y)
                .orElseThrow(() -> new RuntimeException("승인된 기업 회원을 찾을 수 없습니다."));
        return eu.getEmployerId();
    }
    @Override
    public List<JobPostingSimpleDto> getMyJobPostings(String employerUserId) {
        String employerId = resolveEmployerId(employerUserId);
        return applicationMapper.findJobPostingsByEmployerId(employerId);
    }

    @Override
    public Page<ApplicationDto> getApplications(ApplicationRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = applicationMapper.getApplicationCount(req);
        List<ApplicationDto> rows = applicationMapper.getApplications(req, offset, safeSize);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public boolean updateStatuses(List<ApplicationDto> updates, String employerUserId) {
        int updatedCount = 0;
        for (ApplicationDto dto : updates) {
            updatedCount += applicationMapper.updateApplicationStatus(dto.getApplicationId(),
                    dto.getStatus(),
                    employerUserId);
        }
        return updatedCount == updates.size();
    }
//    @Override
//    public Map<String, Object> getApplicationPreview(Integer applicationId, String employerUserId) {
//        // 1. 지원 내역 조회 (해당 기업 소속 공고인지 체크)
//        ApplicationDto app = applicationMapper.findByIdAndEmployerUserId(applicationId, employerUserId);
//        if (app == null) {
//            throw new CareerLinkException("지원 내역을 찾을 수 없습니다.");
//        }
//
//
//        // 2. 이력서 조회 (항목 포함)
//        ResumeDto resume = resumeMapper.findDetailById(app.getResumeId());
//
//        // 3. 자소서 조회 (항목 포함)
//        CoverLetterDto coverLetter = null;
//        if (app.getCoverLetterId() != null) {
//            coverLetter = coverLetterMapper.findDetailById(app.getCoverLetterId());
//        }
//
//        // 4. 리턴 (프론트는 { resume, coverLetter } 그대로 받음)
//        Map<String, Object> result = new HashMap<>();
//        result.put("resume", resume);
//        result.put("coverLetter", coverLetter);
//        return result;
//    }


}