package com.career.careerlink.users.repository;

import com.career.careerlink.users.entity.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginUserRepository extends JpaRepository<LoginUser, String> {
    Optional<LoginUser> findByLoginId(String loginId);
}
