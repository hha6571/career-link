package com.career.careerlink.employers.controller;

import com.career.careerlink.employers.dto.*;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.global.response.SkipWrap;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
@Validated
public class EmployerController {

    private final EmployerService employerService;

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
}