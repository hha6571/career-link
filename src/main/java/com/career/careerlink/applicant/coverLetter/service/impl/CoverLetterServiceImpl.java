package com.career.careerlink.applicant.coverLetter.service.impl;

import com.career.careerlink.applicant.coverLetter.dto.CoverLetterDto;
import com.career.careerlink.applicant.coverLetter.entity.CoverLetter;
import com.career.careerlink.applicant.coverLetter.repository.CoverLetterRepository;
import com.career.careerlink.applicant.coverLetter.service.CoverLetterService;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoverLetterServiceImpl implements CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CoverLetterDto> getMyCoverLetters() {
        String userId = getCurrentUserId();
        List<CoverLetter> entities = coverLetterRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return CoverLetterDto.listOf(entities); // items = []
    }

    @Override
    @Transactional(readOnly = true)
    public CoverLetterDto getMyCoverLetter(Integer coverLetterId) {
        String userId = getCurrentUserId();
        CoverLetter entity = coverLetterRepository.findByCoverLetterIdAndUserId(coverLetterId, userId)
                .orElseThrow(() -> new RuntimeException("자소서를 찾을 수 없습니다."));
        return CoverLetterDto.of(entity); // items 포함
    }

    @Override
    public CoverLetterDto createCoverLetter(CoverLetterDto dto) {
        String userId = getCurrentUserId();
        CoverLetter entity = dto.toEntity(userId);
        return CoverLetterDto.of(coverLetterRepository.save(entity));
    }

    @Override
    public CoverLetterDto updateCoverLetter(Integer coverLetterId, CoverLetterDto dto) {
        String userId = getCurrentUserId();
        CoverLetter entity = coverLetterRepository.findByCoverLetterIdAndUserId(coverLetterId, userId)
                .orElseThrow(() -> new RuntimeException("자소서를 찾을 수 없습니다."));

        dto.updateEntity(entity, userId);
        return CoverLetterDto.of(coverLetterRepository.save(entity));
    }

    @Override
    public void deleteCoverLetter(Integer coverLetterId) {
        String userId = getCurrentUserId();
        CoverLetter entity = coverLetterRepository.findByCoverLetterIdAndUserId(coverLetterId, userId)
                .orElseThrow(() -> new RuntimeException("자소서를 찾을 수 없습니다."));
        coverLetterRepository.delete(entity);
    }

    // userId 공통 유틸
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CareerLinkException("로그인이 필요합니다.");
        }
        return authentication.getName();
    }

}
