package com.career.careerlink.applicant.mapper;

import com.career.careerlink.applicant.dto.ResumeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ResumeMapper {
    ResumeDto findDetailById(@Param("resumeId") Integer resumeId);
}
