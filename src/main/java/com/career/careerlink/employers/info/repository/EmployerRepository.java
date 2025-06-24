package com.career.careerlink.employers.info.repository;

import com.career.careerlink.employers.info.entiry.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, String>, JpaSpecificationExecutor<Employer> {
    boolean existsByBizRegNo(String bizRegNo);
    Optional<Employer> findByEmployerId(String employerId);
}
