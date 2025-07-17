package com.career.careerlink.global.security;

import com.career.careerlink.applicant.entity.Applicant;
import com.career.careerlink.applicant.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ApplicantRepository applicantRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Applicant applicant = applicantRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        return User.builder()
                .username(String.valueOf(applicant.getUserId()))
                .password(applicant.getPassword())
                .roles("USER") // 권한 설정
                .build();
    }
}
