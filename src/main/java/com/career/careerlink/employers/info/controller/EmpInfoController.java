package com.career.careerlink.employers.info.controller;

import com.career.careerlink.employers.info.dto.EmployerInformationDto;
import com.career.careerlink.employers.info.dto.EmployerRegistrationDto;
import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.employers.info.service.EmpInfoService;
import com.career.careerlink.global.response.SkipWrap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
@Validated
public class EmpInfoController {

    private final EmpInfoService empInfoService;

    /**
     * 기업중복방지 체크
     * @param bizRegNo
     */
    @SkipWrap
    @GetMapping("/check-bizRegNo")
    public Map<String, Boolean> checkBizRegNo(@RequestParam String bizRegNo) {
        boolean exists = empInfoService.isCompanyDuplicate(bizRegNo);
        return Map.of("exists", exists);
    }

    /**
     * 기업등록요청
     * @param dto
     * @param file
     */
    @PostMapping(value = "/registration-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void companyRegistrationRequest(@RequestPart("dto") EmployerRegistrationDto dto, @RequestPart("file") MultipartFile file) {
        empInfoService.companyRegistrationRequest(dto, file);
    }

    /**
     * 기업정보 조회
     */
    @GetMapping("/info")
    public EmployerInformationDto getCompanyInfomation() {
        return empInfoService.getCompanyInformation();
    }

    /**
     * 기업정보저장
     * @param dto
     * @param companyLogo
     */
    @PutMapping(value = "/info/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmployerInformationDto saveEmployerInfo(@RequestPart("dto") EmployerInformationDto dto, @RequestPart(value = "companyLogo", required = false) MultipartFile companyLogo) {
        Employer saved = empInfoService.saveEmployerInfo(dto, companyLogo);
        return new EmployerInformationDto(saved);
    }

    /**
     * 기업 로고 삭제(로고사진 삭제시만)
     */
    @DeleteMapping("/info/logo")
    public void deleteCompanyLogo() {
        empInfoService.deleteCompanyLogo();
    }

}