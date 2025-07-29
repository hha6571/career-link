package com.career.careerlink.employers.controller;

import com.career.careerlink.employers.dto.EmployerInfomationDto;
import com.career.careerlink.employers.service.EmployerService;
import com.career.careerlink.employers.dto.EmployerRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    // 등록된 기업 확인 (중복등록 방지용)
    @GetMapping("/check-bizRegNo")
    public ResponseEntity<Map<String, Boolean>> checkBizRegNo(@RequestParam String bizRegNo) {
        boolean exists = employerService.isCompanyDuplicate(bizRegNo);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 기업등록요청
    @PostMapping("/registration-requests")
    public ResponseEntity<Void> companyRegistrationRequest(@RequestBody EmployerRegistrationDto dto) {
        employerService.companyRegistrationRequest(dto);
        return ResponseEntity.ok().build();
    }

    // 기업 기본정보 조회
    @GetMapping("/info")
    public EmployerInfomationDto getCompanyInfomation(@RequestParam String employerId) {
        return employerService.getCompanyInfomation(employerId);
    }
}