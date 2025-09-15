package com.career.careerlink.employers.jobPosting.service.impl;

import com.career.careerlink.employers.jobPosting.dto.ApplicationDto;
import com.career.careerlink.employers.jobPosting.dto.ApplicationRequestDto;
import com.career.careerlink.employers.jobPosting.dto.JobPostingSimpleDto;
import com.career.careerlink.employers.jobPosting.mapper.ApplicationMapper;
import com.career.careerlink.employers.jobPosting.service.EmpJobPostingService;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.users.entity.EmployerUsers;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpJobPostingServiceImpl implements EmpJobPostingService {

    private final EmployerUserRepository employerUserRepository;
    private final ApplicationMapper applicationMapper;


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