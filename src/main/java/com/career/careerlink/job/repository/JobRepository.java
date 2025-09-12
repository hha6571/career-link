package com.career.careerlink.job.repository;

import com.career.careerlink.job.dto.JobPostingResponse;
import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobRepository
        extends JpaRepository<JobPosting, Integer>, JpaSpecificationExecutor<JobPosting> {
    Optional<JobPosting> findByJobPostingIdAndIsActive(int jobPostingId, AgreementStatus isActive);
    
    //    조회수 증가
    @Modifying
    @Query("UPDATE JobPosting jp SET jp.viewCount = jp.viewCount + 1 WHERE jp.jobPostingId = :jobPostingId")
    void incrementViewCount(@Param("jobPostingId") int jobPostingId);
    
    //    상세보기
    @Query("""
      select new com.career.careerlink.job.dto.JobPostingResponse(
        jp.jobPostingId, jp.title, jp.description, jp.employerId, e.companyName,
        jp.jobFieldCode, jp.educationLevelCode, jp.locationCode,
        jp.employmentTypeCode, jp.careerLevelCode, jp.salaryCode,
        jp.applicationDeadline, jp.isActive, jp.isDeleted
      )
      from JobPosting jp
      join Employer e on e.employerId = jp.employerId
      where jp.jobPostingId = :id
          and jp.isDeleted = 'N'
    """)
    Optional<JobPostingResponse> findDetailJobPosting(@Param("id") Integer id);
}
