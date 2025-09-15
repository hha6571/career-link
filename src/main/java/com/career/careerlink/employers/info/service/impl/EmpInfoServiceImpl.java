package com.career.careerlink.employers.info.service.impl;

import com.career.careerlink.employers.info.dto.EmployerInformationDto;
import com.career.careerlink.employers.info.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.employers.info.repository.EmployerRepository;
import com.career.careerlink.employers.info.service.EmpInfoService;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.s3.S3Service;
import com.career.careerlink.global.s3.S3UploadType;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.users.entity.EmployerUsers;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmpInfoServiceImpl implements EmpInfoService {

    private final EmployerRepository employerRepository;
    private final EmployerUserRepository employerUserRepository;
    private final S3Service s3Service;
    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;

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
}