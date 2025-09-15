package com.career.careerlink.applicant.mapper;

import com.career.careerlink.applicant.dto.CoverLetterDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CoverLetterMapper {
    CoverLetterDto findDetailById(@Param("coverLetterId") Integer coverLetterId);
}
