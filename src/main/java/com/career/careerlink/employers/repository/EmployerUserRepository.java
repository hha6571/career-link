package com.career.careerlink.employers.repository;

import com.career.careerlink.employers.entity.EmployerUsers;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerUserRepository extends JpaRepository<EmployerUsers, String> {
    long countByEmployerId(String employerId);
    Optional<EmployerUsers> findByEmployerUserId(String employerUserId);
    Optional<EmployerUsers> findByEmployerUserIdAndIsApproved(String employerUserId, AgreementStatus isApproved);
}