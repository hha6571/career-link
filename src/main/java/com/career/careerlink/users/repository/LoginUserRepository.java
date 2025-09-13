package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.LoginUser;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoginUserRepository extends JpaRepository<LoginUser, String> {
    Optional<LoginUser> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    Optional<LoginUser> findByEmail(String email);
    Optional<LoginUser> findByUserNameAndEmail(String userName, String email);
    Optional<LoginUser> findByUserNameAndEmailAndLoginId(String userName, String email, String loginId);
    @Query("select u.userName from LoginUser u where u.loginId = :loginId")
    Optional<String> findUserNameByLoginId(@Param("loginId") String loginId);
    @Query("select u.email from LoginUser u where u.loginId = :loginId")
    Optional<String> findEmailByLoginId(@Param("loginId") String loginId);
}

