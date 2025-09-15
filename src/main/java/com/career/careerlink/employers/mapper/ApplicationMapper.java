package com.career.careerlink.employers.mapper;

import com.career.careerlink.applicant.dto.ApplicationResponseDto;
import com.career.careerlink.employers.dto.ApplicationDto;
import com.career.careerlink.employers.dto.ApplicationRequestDto;
import com.career.careerlink.employers.dto.JobPostingSimpleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ApplicationMapper {
    long getApplicationCount(@Param("req") ApplicationRequestDto req);
    List<ApplicationDto> getApplications(@Param("req") ApplicationRequestDto req,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
    int updateApplicationStatus(@Param("applicationId") Integer applicationId,
                                @Param("status") String status,
                                @Param("updatedBy") String updatedBy);
    List<JobPostingSimpleDto> findJobPostingsByEmployerId(@Param("employerId") String employerId);
    ApplicationResponseDto findByIdAndEmployer(@Param("applicationId") Integer applicationId,
                                               @Param("employerUserId") String employerUserId);
    /**
     * 기업 전용 - 본인 소속 공고의 지원 내역 조회
     */
//    Optional<ApplicationDto> findApplicationForEmployer(
//            @Param("applicationId") Integer applicationId,
//            @Param("employerId") String employerId
//    );
//    ApplicationDto findByIdAndEmployerUserId(
//            @Param("applicationId") Integer applicationId,
//            @Param("employerUserId") String employerUserId
//    );
//
//    // (선택) Employer “회사” 기준으로 검증
//    ApplicationDto findByIdAndEmployerId(
//            @Param("applicationId") Integer applicationId,
//            @Param("employerId") String employerId
//    );
}

