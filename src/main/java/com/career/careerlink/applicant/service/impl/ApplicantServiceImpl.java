package com.career.careerlink.applicant.service.impl;

import com.career.careerlink.applicant.dto.ApplicantDto;
import com.career.careerlink.applicant.dto.ApplicantRequestPassWordDto;
import com.career.careerlink.applicant.dto.SignupRequestDto;
import com.career.careerlink.applicant.entity.Applicant;
import com.career.careerlink.applicant.repository.ApplicantRepository;
import com.career.careerlink.applicant.service.ApplicantService;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.util.UserIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(SignupRequestDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPasswordHash());
        String generatedUserId = UserIdGenerator.generate("USR");

        Applicant newApplicant = Applicant.builder()
                .userId(generatedUserId)
                .loginId(dto.getLoginId())
                .passwordHash(encodedPassword)
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .userType(dto.getUserType())
                .email(dto.getEmail())
                .lastLoginAt(dto.getLastLoginAt())
                .dormantAt(dto.getDormantAt())
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .userStatus(dto.getUserStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        applicantRepository.save(newApplicant);
    }

    @Override
    public ApplicantDto getProfile() {
        String userId = getCurrentUserId();
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CareerLinkException("회원 정보를 찾을 수 없습니다."));
        return ApplicantDto.of(applicant);
    }

    @Override
    @Transactional
    public ApplicantDto updateProfile(ApplicantDto dto) {
        String userId = getCurrentUserId();
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CareerLinkException("회원 정보를 찾을 수 없습니다."));

        dto.updateEntity(applicant); // phoneNumber, gender, agreeMarketing
        return ApplicantDto.of(applicant);
    }

    @Override
    @Transactional
    public void changePassword(ApplicantRequestPassWordDto requestPassWordDto) {
        String userId = getCurrentUserId();
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CareerLinkException("회원 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestPassWordDto.getCurrentPassword(), applicant.getPasswordHash())) {
            throw new CareerLinkException("현재 비밀번호가 올바르지 않습니다.");
        }

        applicant.setPasswordHash(passwordEncoder.encode(requestPassWordDto.getNewPassword()));
        applicant.setUpdatedAt(LocalDateTime.now());

    }

    @Override
    @Transactional
    public void withdraw() {
        String userId = getCurrentUserId();
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CareerLinkException("회원 정보를 찾을 수 없습니다."));

        applicant.withdraw();
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CareerLinkException("로그인이 필요합니다.");
        }
        return authentication.getName();
    }

}
