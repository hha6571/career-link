package com.career.careerlink.job.repository;

import com.career.careerlink.job.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository
        extends JpaRepository<JobPosting, Integer>, JpaSpecificationExecutor<JobPosting> {
}
