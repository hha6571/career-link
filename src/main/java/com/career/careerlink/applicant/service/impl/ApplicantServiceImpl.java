package com.career.careerlink.applicant.service.impl;

import com.career.careerlink.applicant.dto.ApplicantDto;
import com.career.careerlink.applicant.dto.ApplicantRequestPassWordDto;
import com.career.careerlink.applicant.entity.Applicant;
import com.career.careerlink.applicant.repository.ApplicantRepository;
import com.career.careerlink.applicant.service.ApplicantService;
import com.career.careerlink.global.exception.CareerLinkException;
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
