package com.career.careerlink.employers.controller;

import com.career.careerlink.employers.dto.EmployerInformationDto;
import com.career.careerlink.employers.dto.EmployerSignupDto;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;
    private final S3Service s3Service;
    private final EmployerRepository employerRepository;

    // 등록된 기업 확인 (중복등록 방지용)
    @SkipWrap
    @GetMapping("/check-bizRegNo")
    public Map<String, Boolean> checkBizRegNo(@RequestParam String bizRegNo) {
        boolean exists = employerService.isCompanyDuplicate(bizRegNo);
        return Map.of("exists", exists);
    }

    // 기업등록요청
    @PostMapping(value = "/registration-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file) {
        employerService.companyRegistrationRequest(dto, file);
    }

    // 기업 기본정보 조회
    @GetMapping("/info")
    public EmployerInformationDto getCompanyInfomation(@RequestParam String employerId) {
        return employerService.getCompanyInformation(employerId);
    }

    //기업정보 저장
    @PutMapping(value = "/info/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmployerInformationDto saveEmployerInfo(@RequestPart("dto") EmployerInformationDto dto, @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo) {
        Employer saved = employerService.saveEmployerInfo(dto, companyLogo);
        return new EmployerInformationDto(saved);
    }

    //기업 로고 삭제(로고사진 삭제시만)
    @DeleteMapping("/info/logo")
    public void deleteCompanyLogo(@RequestParam String employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("기업을 찾을 수 없습니다."));

        if (employer.getCompanyLogoUrl() != null) {
            s3Service.deleteFileByUrl(employer.getCompanyLogoUrl());
            employer.setCompanyLogoUrl(null);
            employerRepository.save(employer);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> empSignup(@RequestBody EmployerSignupDto dto) {
        employerService.empSignup(dto);
        return ResponseEntity.ok().build();
    }
}