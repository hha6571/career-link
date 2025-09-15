package com.career.careerlink.job.mapper;

import com.career.careerlink.admin.jobPosting.dto.AdminJobPostingResponse;
import com.career.careerlink.admin.jobPosting.dto.AdminJobPostingSearchRequest;
import com.career.careerlink.employers.jobPosting.dto.EmployerJobPostingResponse;
import com.career.careerlink.employers.jobPosting.dto.EmployerJobPostingSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface JobPostingMapper {

    long searchForEmployerCount(
            @Param("req") EmployerJobPostingSearchRequest req,
            @Param("employerUserId") String employerUserId
    );

    List<EmployerJobPostingResponse> searchForEmployer(
            @Param("req") EmployerJobPostingSearchRequest req,
            @Param("employerUserId") String employerUserId,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort,
            @Param("direction") String direction
    );

    int deleteBulkByEmployer(@Param("targetJobPostingIds") List<String> targetJobPostingIds,
                             @Param("employerUserId") String employerUserId);

    long searchForAdminCount(
            @Param("req") AdminJobPostingSearchRequest req
    );

    List<AdminJobPostingResponse> searchForAdmin(
            @Param("req") AdminJobPostingSearchRequest req,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort,
            @Param("direction") String direction
    );

    int deleteBulkByAdmin(@Param("targetJobPostingIds") List<String> targetJobPostingIds);

    List<Map<String, Object>> selectHotJobs(
            @Param("limit") int limit,
            @Param("cursorViewCount") Integer cursorViewCount,
            @Param("cursorId") Integer cursorId
    );
}
