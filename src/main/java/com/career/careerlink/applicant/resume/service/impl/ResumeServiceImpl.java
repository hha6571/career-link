package com.career.careerlink.applicant.resume.service.impl;

import com.career.careerlink.applicant.resume.dto.*;
import com.career.careerlink.applicant.resume.entity.*;
import com.career.careerlink.applicant.resume.repository.*;
import com.career.careerlink.applicant.resume.service.ResumeService;
import com.career.careerlink.global.exception.CareerLinkException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final CertificationRepository certificationRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public ResumeDto createResume(ResumeFormDto dto) {
        String userId = getCurrentUserId();

        Resume resume = dto.toEntity(userId);
        Resume saved = resumeRepository.save(resume);

        saveChildren(saved, dto, userId);

        return ResumeDto.of(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeDto getResume(Integer resumeId) {
        String userId = getCurrentUserId();

        Resume resume = resumeRepository.findByResumeIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new CareerLinkException("이력서를 찾을 수 없습니다."));

        return ResumeDto.of(resume);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeDto> getMyResumes() {
        String userId = getCurrentUserId();
        return ResumeDto.listOf(resumeRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public ResumeDto updateResume(Integer resumeId, ResumeFormDto dto) {
        String userId = getCurrentUserId();

        Resume resume = resumeRepository.findByResumeIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new CareerLinkException("이력서를 찾을 수 없습니다."));

        //dto.updateEntity(resume, userId);
        resume.setTitle(dto.getTitle());
        resume.setIsActive(dto.getIsActive());
        resume.setUpdatedBy(userId);

        syncEducations(resume, dto.getEducations(), userId);
        syncExperiences(resume, dto.getExperiences(), userId);
        syncCertifications(resume, dto.getCertifications(), userId);
        syncSkills(resume, dto.getSkills(), userId);
        //syncCoverLetters(resume, dto.getCoverLetters(), userId);

        return ResumeDto.of(resume);
    }

    @Override
    public void deleteResume(Integer resumeId) {
        String userId = getCurrentUserId();

        Resume resume = resumeRepository.findByResumeIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new CareerLinkException("이력서를 찾을 수 없습니다."));

        resumeRepository.delete(resume);
    }


    // resume 내부 유틸
    private void saveChildren(Resume resume, ResumeFormDto dto, String userId) {
        if (dto.getEducations() != null) {
            educationRepository.saveAll(dto.getEducations().stream()
                    .map(e -> e.toEntity(resume, userId)).toList());
        }
        if (dto.getExperiences() != null) {
            experienceRepository.saveAll(dto.getExperiences().stream()
                    .map(e -> e.toEntity(resume, userId)).toList());
        }
        if (dto.getCertifications() != null) {
            certificationRepository.saveAll(dto.getCertifications().stream()
                    .map(c -> c.toEntity(resume, userId)).toList());
        }
        if (dto.getSkills() != null) {
            skillRepository.saveAll(dto.getSkills().stream()
                    .map(s -> s.toEntity(resume, userId)).toList());
        }
    }
    // null 리스트가 오면 "모두 삭제"로 간주.
    private void syncEducations(Resume resume, List<EducationDto> incoming, String userId) {
        Set<Education> current = resume.getEducations();
        if (incoming == null) { current.clear(); return; }

        Map<Integer, Education> byId = current.stream()
                .filter(e -> e.getEducationId() != null)
                .collect(Collectors.toMap(Education::getEducationId, e -> e));

        Set<Integer> keepIds = new HashSet<>();

        for (EducationDto dto : incoming) {
            Integer id = dto.getEducationId();
            if (id == null) {
                // CREATE
                current.add(dto.toEntity(resume, userId));
            } else {
                // UPDATE (존재하는 것만)
                Education found = byId.get(id);
                if (found != null) {
                    dto.updateEntity(found, userId);
                    keepIds.add(id);
                }
                // else: 다른 이력서의 ID이거나 존재하지 않는 ID → 무시(필요시 예외 처리)
            }
        }

        // DELETE (요청에 없는 기존 것들만 제거)
        current.removeIf(e -> e.getEducationId() != null && !keepIds.contains(e.getEducationId()));
    }

    private void syncExperiences(Resume resume, List<ExperienceDto> incoming, String userId) {
        Set<Experience> current = resume.getExperiences();
        if (incoming == null) { current.clear(); return; }

        Map<Integer, Experience> byId = current.stream()
                .filter(e -> e.getExperienceId() != null)
                .collect(Collectors.toMap(Experience::getExperienceId, e -> e));

        Set<Integer> keepIds = new HashSet<>();

        for (ExperienceDto dto : incoming) {
            Integer id = dto.getExperienceId();
            if (id == null) {
                current.add(dto.toEntity(resume, userId));
            } else {
                Experience found = byId.get(id);
                if (found != null) {
                    dto.updateEntity(found, userId);
                    keepIds.add(id);
                }
            }
        }

        current.removeIf(e -> e.getExperienceId() != null && !keepIds.contains(e.getExperienceId()));
    }

    private void syncCertifications(Resume resume, List<CertificationDto> incoming, String userId) {
        Set<Certification> current = resume.getCertifications();
        if (incoming == null) { current.clear(); return; }

        Map<Integer, Certification> byId = current.stream()
                .filter(e -> e.getCertificationId() != null)
                .collect(Collectors.toMap(Certification::getCertificationId, e -> e));

        Set<Integer> keepIds = new HashSet<>();

        for (CertificationDto dto : incoming) {
            Integer id = dto.getCertificationId();
            if (id == null) {
                current.add(dto.toEntity(resume, userId));
            } else {
                Certification found = byId.get(id);
                if (found != null) {
                    dto.updateEntity(found, userId);
                    keepIds.add(id);
                }
            }
        }

        current.removeIf(e -> e.getCertificationId() != null && !keepIds.contains(e.getCertificationId()));
    }

    private void syncSkills(Resume resume, List<SkillDto> incoming, String userId) {
        Set<Skill> current = resume.getSkills();
        if (incoming == null) { current.clear(); return; }

        Map<Integer, Skill> byId = current.stream()
                .filter(e -> e.getSkillId() != null)
                .collect(Collectors.toMap(Skill::getSkillId, e -> e));

        Set<Integer> keepIds = new HashSet<>();

        for (SkillDto dto : incoming) {
            Integer id = dto.getSkillId();
            if (id == null) {
                current.add(dto.toEntity(resume, userId));
            } else {
                Skill found = byId.get(id);
                if (found != null) {
                    dto.updateEntity(found, userId);
                    keepIds.add(id);
                }
            }
        }

        current.removeIf(e -> e.getSkillId() != null && !keepIds.contains(e.getSkillId()));
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
