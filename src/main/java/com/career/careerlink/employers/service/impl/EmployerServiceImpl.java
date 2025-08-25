package com.career.careerlink.employers.service.impl;
import com.career.careerlink.employers.dto.EmployerInformationDto;
import com.career.careerlink.employers.dto.EmployerSignupDto;
import com.career.careerlink.employers.entity.EmployerUser;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.employers.repository.EmployerUserRepository;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.global.s3.S3Service;
import com.career.careerlink.global.s3.S3UploadType;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.users.entity.Applicant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final EmployerUserRepository employerUserRepository;
    private final S3Service s3Service;
    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isCompanyDuplicate(String bizRegNo) {
        return employerRepository.existsByBizRegNo(bizRegNo);
    }

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

    @Override
    public EmployerInformationDto getCompanyInformation(String employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기업이 존재하지 않습니다: " + employerId));

        return EmployerInformationDto.builder()
                .employerId(employer.getEmployerId())
                .companyTypeCode(employer.getCompanyTypeCode())
                .companyName(employer.getCompanyName())
                .bizRegNo(employer.getBizRegNo())
                .bizRegistrationUrl(employer.getBizRegistrationUrl())
                .ceoName(employer.getCeoName())
                .companyPhone(employer.getCompanyPhone())
                .companyEmail(employer.getCompanyEmail())
                .baseAddress(employer.getBaseAddress())
                .detailAddress(employer.getDetailAddress())
                .postcode(employer.getPostcode())
                .establishedDate(employer.getEstablishedDate())
                .industryCode(employer.getIndustryCode())
                .companyIntro(employer.getCompanyIntro())
                .homepageUrl(employer.getHomepageUrl())
                .companyLogoUrl(employer.getCompanyLogoUrl())
                .employeeCount(employer.getEmployeeCount())
                .build();
    }


    @Override
    public Employer saveEmployerInfo(EmployerInformationDto dto, MultipartFile companyLogo) {
        Employer employer = (dto.getEmployerId() != null)
                ? employerRepository.findById(dto.getEmployerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다. id=" + dto.getEmployerId()))
                : new Employer();

        // 로고 업로드(있을 때만). 기존 로고 교체 정책이 있으면 oldUrl 삭제 처리 추가
        if (companyLogo != null && !companyLogo.isEmpty()) {
            String oldUrl = employer.getCompanyLogoUrl();
            String newUrl = s3Service.uploadFile(S3UploadType.COMPANY_LOGO, companyLogo);
            employer.setCompanyLogoUrl(newUrl);

            // 이전 파일 삭제
            if (oldUrl != null) {
                s3Service.deleteFileByUrl(oldUrl);
            }
        }

        employer.setCompanyPhone(dto.getCompanyPhone());
        employer.setBaseAddress(dto.getBaseAddress());
        employer.setDetailAddress(dto.getDetailAddress());
        employer.setPostcode(dto.getPostcode());
        employer.setIndustryCode(dto.getIndustryCode());
        employer.setCompanyIntro(dto.getCompanyIntro());
        employer.setHomepageUrl(dto.getHomepageUrl());
        employer.setCompanyTypeCode(dto.getCompanyTypeCode());
        employer.setEmployeeCount(dto.getEmployeeCount());
        employer.setEstablishedDate(dto.getEstablishedDate());

        return employerRepository.save(employer);
    }

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

        EmployerUser user = EmployerUser.builder()
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
                .isApproved(dto.getIsApproved())
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .employerStatus(dto.getEmployerStatus())
                .build();

        employerUserRepository.save(user);
    }
}