package com.career.careerlink.employers.repository;

import com.career.careerlink.employers.entity.EmployerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerUserRepository extends JpaRepository<EmployerUser, String> {
    long countByEmployerId(String employerId);
}