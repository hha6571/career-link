package com.career.careerlink.applicant.resume.controller;

import com.career.careerlink.applicant.resume.dto.ResumeDto;
import com.career.careerlink.applicant.resume.dto.ResumeFormDto;
import com.career.careerlink.applicant.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    /**
     * 이력서
     */
    // 이력서 단건 조회
    @GetMapping("/resume/getResume/{resumeId}")
    public ResumeDto getResume(@PathVariable Integer resumeId) {
        return resumeService.getResume(resumeId);
    }

    // 내 이력서 전체 조회
    @GetMapping("/resume/getMyResumes")
    public List<ResumeDto> getMyResumes() {
        return resumeService.getMyResumes();
    }

    @PostMapping("/resume/createResume")
    public ResumeDto createResume(@RequestBody ResumeFormDto dto) {
        return resumeService.createResume(dto);
    }

    @PutMapping("/resume/updateResume/{resumeId}")
    public ResumeDto updateResume(@PathVariable Integer resumeId, @RequestBody ResumeFormDto dto) {
        return resumeService.updateResume(resumeId, dto);
    }

    @DeleteMapping("/resume/deleteResume/{resumeId}")
    public void deleteResume(@PathVariable Integer resumeId) {
        resumeService.deleteResume(resumeId);
    }

}
