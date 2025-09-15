package com.career.careerlink.employers.member.service.impl;

import com.career.careerlink.employers.member.dto.EmployerMemberDto;
import com.career.careerlink.employers.member.dto.EmployerMemberSearchRequest;
import com.career.careerlink.employers.member.dto.EmployerSignupDto;
import com.career.careerlink.employers.member.mapper.EmployerMemberMapper;
import com.career.careerlink.employers.member.service.EmpMemberService;
import com.career.careerlink.employers.member.repository.EmployerUserRepository;
import com.career.careerlink.global.util.UserIdGenerator;
import com.career.careerlink.users.entity.EmployerUsers;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpMemberServiceImpl implements EmpMemberService {

    private final EmployerUserRepository employerUserRepository;
    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;
    private final EmployerMemberMapper employerMemberMapper;

    /**
     * 기업회원가입
     * @param dto
     */
    @Override
    @Transactional
    public void empSignup(EmployerSignupDto dto) {
        Objects.requireNonNull(dto.getEmployerId(), "employerId is required");
        Objects.requireNonNull(dto.getEmployerLoginId(), "employerLoginId is required");
        Objects.requireNonNull(dto.getPasswordHash(), "password is required");

        String encodedPassword = passwordEncoder.encode(dto.getPasswordHash());

        em.createQuery("select e from Employer e where e.employerId = :id")
                .setParameter("id", dto.getEmployerId())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult();

        long count = employerUserRepository.countByEmployerId(dto.getEmployerId());
        String role = (count == 0) ? "ADMIN" : "EMPLOYEE";

        String generatedUserId = UserIdGenerator.generate("EMU");

        // 승인여부 결정: 첫 가입자(ADMIN)는 무조건 Y
        AgreementStatus approvedStatus = (count == 0)
                ? AgreementStatus.Y
                : dto.getIsApproved();

        EmployerUsers user = EmployerUsers.builder()
                .employerUserId(generatedUserId)
                .employerId(dto.getEmployerId())
                .employerLoginId(dto.getEmployerLoginId())
                .passwordHash(encodedPassword)
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .birthDate(dto.getBirthDate())
                .email(dto.getEmail())
                .role(role)
                .deptName(dto.getDeptName())
                .isApproved(approvedStatus)
                .agreeTerms(dto.getAgreeTerms())
                .agreePrivacy(dto.getAgreePrivacy())
                .agreeMarketing(dto.getAgreeMarketing())
                .employerStatus(dto.getEmployerStatus())
                .build();

        employerUserRepository.save(user);
    }


    @Override
    public Page<EmployerMemberDto> getEmployerMembers(EmployerMemberSearchRequest req, String employerUserId) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        // 정렬 방향 보정
        String direction = Optional.ofNullable(req.getDirection())
                .orElse("asc")
                .toUpperCase(Locale.ROOT);
        direction = "DESC".equals(direction) ? "DESC" : "ASC";

        // 정렬 필드 보정(화이트리스트 키)
        String sort = Optional.ofNullable(req.getSort()).orElse("employerUserId");

        long total = employerMemberMapper.membersCount(req, employerUserId);
        List<EmployerMemberDto> rows = employerMemberMapper.members(req, employerUserId, offset, safeSize, sort, direction);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public int approveOne(String targetEmployerUserId, String employerUserId){
        return employerMemberMapper.approveIfPending(
                targetEmployerUserId,
                employerUserId
        );
    }

    @Override
    @Transactional
    public int approveBulk(List<String> targetEmployerUserIds, String employerUserId){
        return employerMemberMapper.approveIfPendingBulk(
                targetEmployerUserIds,
                employerUserId
        );
    }

}