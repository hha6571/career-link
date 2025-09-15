package com.career.careerlink.applicant.coverLetter.service;

import com.career.careerlink.applicant.coverLetter.dto.*;
import java.util.List;

public interface CoverLetterService {
    List<CoverLetterDto> getMyCoverLetters();
    CoverLetterDto getMyCoverLetter(Integer coverLetterId);
    CoverLetterDto createCoverLetter(CoverLetterDto dto);
    CoverLetterDto updateCoverLetter(Integer coverLetterId, CoverLetterDto dto);
    void deleteCoverLetter(Integer coverLetterId);
}
