package com.career.careerlink.applicant.coverLetter.controller;

import com.career.careerlink.applicant.coverLetter.dto.CoverLetterDto;
import com.career.careerlink.applicant.coverLetter.service.CoverLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    /**
     * 자소서
     */
    @GetMapping("/coverLetter/getMyCoverLetters")
    public List<CoverLetterDto> getMyCoverLetters() {
        return coverLetterService.getMyCoverLetters();
    }
    @GetMapping("/coverLetter/getMyCoverLetter/{coverLetterId}")
    public CoverLetterDto getMyCoverLetter(@PathVariable("coverLetterId") Integer coverLetterId) {
        return coverLetterService.getMyCoverLetter(coverLetterId);
    }
    @PostMapping("/coverLetter/createCoverLetter")
    public CoverLetterDto createCoverLetter(@RequestBody CoverLetterDto dto) {
        return coverLetterService.createCoverLetter(dto);
    }
    @PutMapping("/coverLetter/updateCoverLetter/{coverLetterId}")
    public CoverLetterDto updateCoverLetter(
            @PathVariable("coverLetterId") Integer coverLetterId,
            @RequestBody CoverLetterDto dto) {
        return coverLetterService.updateCoverLetter(coverLetterId, dto);
    }
    @DeleteMapping("/coverLetter/deleteCoverLetter/{coverLetterId}")
    public void deleteCoverLetter(@PathVariable("coverLetterId") Integer coverLetterId) {
        coverLetterService.deleteCoverLetter(coverLetterId);
    }

}
