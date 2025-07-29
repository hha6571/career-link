package com.career.careerlink.employers.repository;

import com.career.careerlink.employers.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployerRepository extends JpaRepository<Employer, String>, JpaSpecificationExecutor<Employer> {
    boolean existsByBizRegNo(String bizRegNo);
}
