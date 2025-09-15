package com.career.careerlink.employers.controller;

import com.career.careerlink.employers.dto.*;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.job.service.JobPostingService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
@Validated
public class EmployerController {

    private final EmployerService employerService;
    private final JobPostingService jobPostingService;

    /**
     * 기업중복방지 체크
     * @param bizRegNo
     */
    @SkipWrap
    @GetMapping("/check-bizRegNo")
    public Map<String, Boolean> checkBizRegNo(@RequestParam String bizRegNo) {
        boolean exists = employerService.isCompanyDuplicate(bizRegNo);
        return Map.of("exists", exists);
    }

    /**
     * 기업등록요청
     * @param dto
     * @param file
     */
    @PostMapping(value = "/registration-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file) {
        employerService.companyRegistrationRequest(dto, file);
    }

    /**
     * 기업회원가입
     * @param dto
     */
    @PostMapping("/signup")
    public void empSignup(@RequestBody EmployerSignupDto dto) {
        employerService.empSignup(dto);
    }

    /**
     * 기업정보 조회
     */
    @GetMapping("/info")
    public EmployerInformationDto getCompanyInfomation() {
        return employerService.getCompanyInformation();
    }

    /**
     * 기업정보저장
     * @param dto
     * @param companyLogo
     */
    @PutMapping(value = "/info/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmployerInformationDto saveEmployerInfo(@RequestPart("dto") EmployerInformationDto dto, @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo) {
        Employer saved = employerService.saveEmployerInfo(dto, companyLogo);
        return new EmployerInformationDto(saved);
    }

    /**
     * 기업 로고 삭제(로고사진 삭제시만)
     */
    @DeleteMapping("/info/logo")
    public void deleteCompanyLogo() {
        employerService.deleteCompanyLogo();
    }

    /**
     * 기업회원 리스트 조회
     */
    @GetMapping("/members")
    public Page<EmployerMemberDto> getEmployerMembers(EmployerMemberSearchRequest req ,Principal principal) {
        String employerUserId = principal.getName();
        return employerService.getEmployerMembers(req, employerUserId);
    }

    /**
     * 기업회원 승인처리 (단건)
     */
    @SkipWrap
    @PostMapping("/members/{targetEmployerUserId}/approve")
    public int approveOne(@PathVariable @NotBlank String targetEmployerUserId, Principal principal) {
        String employerUserId = principal.getName();
        return employerService.approveOne(targetEmployerUserId, employerUserId);
    }

    /**
     * 기업회원 승인처리 (다건)
     */
    @SkipWrap
    @PostMapping("/members/approve-bulk")
    public int approveBulk(@RequestBody List<String> targetEmployerUserIds, Principal principal) {
        String employerUserId = principal.getName();
        return employerService.approveBulk(targetEmployerUserIds, employerUserId);
    }

    /**
     * 기업공고 리스트 조회
     */
    @GetMapping("/job-postings/manage")
    public Page<EmployerJobPostingResponse> getJobPostingList(EmployerJobPostingSearchRequest req, Principal principal) {
        String employerUserId = principal.getName();
        return jobPostingService.searchForEmployer(req, employerUserId);
    }

    /**
     * 기업공고 삭제처리 (다건)
     */
    @SkipWrap
    @PostMapping("/job-postings/delete-bulk")
    public int jobPostingDeleteBulk(@RequestBody List<String> targetJobPostingIds, Principal principal) {
        String employerUserId = principal.getName();
        return jobPostingService.deleteBulkByEmployer(targetJobPostingIds, employerUserId);
    }
    /**
     * 기업 공고 셀렉트박스용 단순 조회
     */
    @GetMapping("/job-postings")
    public List<JobPostingSimpleDto> getMyJobPostings(Principal principal) {
        String employerUserId = principal.getName();
        return employerService.getMyJobPostings(employerUserId);
    }
    /**
     * 지원자 목록 조회 (기업 소속 공고 기준)
     */
    @GetMapping("/applications")
    public Page<ApplicationDto> getApplications(ApplicationRequestDto req) {
        return employerService.getApplications(req);
    }

    /**
     * 지원 상태 업데이트
     */
    @PutMapping("/applications/status")
    public boolean updateApplicationStatuses(@RequestBody List<ApplicationDto> updates,
                                             Principal principal) {
        String employerUserId = principal.getName();
        return employerService.updateStatuses(updates, employerUserId);
    }
    /**
     * 지원서 미리보기 (기업 전용)
     */
//    @GetMapping("/applications/{applicationId}/preview")
//    public Map<String, Object> getApplicationPreview(@PathVariable Integer applicationId,
//                                                     Principal principal) {
//        String employerUserId = principal.getName();
//        return employerService.getApplicationPreview(applicationId, employerUserId);
//    }
}