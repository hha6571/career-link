package com.career.careerlink.applicant.service.impl;

import com.career.careerlink.applicant.dto.*;
import com.career.careerlink.applicant.entity.*;
import com.career.careerlink.applicant.entity.enums.ApplicationStatus;
import com.career.careerlink.applicant.repository.*;
import com.career.careerlink.applicant.service.ApplicantService;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResumeRepository resumeRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final CertificationRepository certificationRepository;
    private final SkillRepository skillRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

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

    @Override
    @Transactional
    public ApplicationDto apply(ApplicationRequestDto requestDto) {
        String userId = getCurrentUserId();

        // 1. 취소되지 않은 지원이 이미 존재하는지 확인
        boolean exists = applicationRepository
                .existsByUserIdAndJobPosting_JobPostingIdAndStatusNot(
                        userId,
                        requestDto.getJobPostingId(),
                        ApplicationStatus.CANCELLED
                );

        if (exists) {
            throw new CareerLinkException("이미 해당 공고에 지원했습니다. (취소 후 다시 지원 가능)");
        }

        // 2. 공고 조회
        JobPosting jobPosting = jobRepository.findById(requestDto.getJobPostingId())
                .orElseThrow(() -> new CareerLinkException("공고를 찾을 수 없습니다."));

        // 3. 이력서 조회
        Resume resume = resumeRepository.findById(requestDto.getResumeId())
                .orElseThrow(() -> new CareerLinkException("이력서를 찾을 수 없습니다."));

        // 4. 자소서 조회 (선택적)
        CoverLetter coverLetter = null;
        if (requestDto.getCoverLetterId() != null) {
            coverLetter = coverLetterRepository.findById(requestDto.getCoverLetterId())
                    .orElseThrow(() -> new CareerLinkException("자소서를 찾을 수 없습니다."));
        }

        // 5. Application 생성 & 저장
        Application application = new ApplicationDto()
                .toEntity(jobPosting, resume, coverLetter, userId);

        return ApplicationDto.of(applicationRepository.save(application));
    }

    // ---------------- 내 지원 내역 ----------------
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getMyApplications() {
        String userId = getCurrentUserId();
        return ApplicationDto.listOf(applicationRepository.findByUserId(userId));
    }

    // ---------------- 공고별 지원 내역 (기업) ----------------
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplicationsByJobPosting(Integer jobPostingId) {
        return ApplicationDto.listOf(applicationRepository.findByJobPosting_JobPostingId(jobPostingId));
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
        List<Education> current = resume.getEducations();
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
        List<Experience> current = resume.getExperiences();
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
        List<Certification> current = resume.getCertifications();
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
        List<Skill> current = resume.getSkills();
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

//    private void syncCoverLetters(Resume resume, List<CoverLetterDto> incoming, String userId) {
//        List<CoverLetter> current = resume.getCoverLetters();
//        if (incoming == null) { current.clear(); return; }
//
//        Map<Integer, CoverLetter> byId = current.stream()
//                .filter(e -> e.getCoverLetterId() != null)
//                .collect(Collectors.toMap(CoverLetter::getCoverLetterId, e -> e));
//
//        Set<Integer> keepIds = new HashSet<>();
//
//        for (CoverLetterDto dto : incoming) {
//            Integer id = dto.getCoverLetterId();
//            if (id == null) {
//                current.add(dto.toEntity(resume, userId));
//            } else {
//                CoverLetter found = byId.get(id);
//                if (found != null) {
//                    dto.updateEntity(found, userId);
//                    keepIds.add(id);
//                }
//            }
//        }
//
//        current.removeIf(e -> e.getCoverLetterId() != null && !keepIds.contains(e.getCoverLetterId()));
//    }

    // userId 공통 유틸
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CareerLinkException("로그인이 필요합니다.");
        }
        return authentication.getName();
    }

}
