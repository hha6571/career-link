package com.career.careerlink.employers.repository;

import com.career.careerlink.employers.entity.EmployerUsers;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerUserRepository extends JpaRepository<EmployerUsers, String> {
    long countByEmployerId(String employerId);
    Optional<EmployerUsers> findEmployerIdByEmployerUserId(String employerUserId);
    Optional<EmployerUsers> findByEmployerUserId(String employerUserId);
    Optional<EmployerUsers> findByEmployerUserIdAndIsApproved(String employerUserId, AgreementStatus isApproved);
    @Modifying(clearAutomatically = true)
    @Query("""
      update EmployerUsers e
         set e.employerStatus = 'ACTIVE',
             e.dormantAt = null,
             e.updatedAt = CURRENT_TIMESTAMP
       where e.employerLoginId = :loginId
    """)
    int reactivateByLoginId(@Param("loginId") String loginId);
}