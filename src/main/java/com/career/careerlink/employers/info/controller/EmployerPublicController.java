package com.career.careerlink.employers.info.controller;

import com.career.careerlink.employers.info.dto.EmployerPublicProfileDto;
import com.career.careerlink.employers.info.service.EmployerPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class EmployerPublicController {

    private final EmployerPublicService employerPublicService;
    /**
     * 기업정보 바로가기
     */
    @GetMapping("/employers/{employerId}")
    public EmployerPublicProfileDto getPublicProfile(@PathVariable String employerId) {
        return employerPublicService.getPublicProfile(employerId);
    }
}