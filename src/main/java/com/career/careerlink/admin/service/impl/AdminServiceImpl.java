package com.career.careerlink.admin.service.impl;

import com.career.careerlink.admin.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.dto.MenuDto;
import com.career.careerlink.admin.entity.Menu;
import com.career.careerlink.admin.repository.AdminRepository;
import com.career.careerlink.admin.repository.MenuRepository;
import com.career.careerlink.admin.service.AdminService;
import com.career.careerlink.admin.spec.EmployerSpecification;
import com.career.careerlink.common.mail.MailService;
import com.career.careerlink.common.response.ServiceResult;
import com.career.careerlink.common.response.SuccessCode;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final EmployerRepository employerRepository;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MenuRepository menuRepository;

    @Override
    public List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest) {
        Specification<Employer> spec = null;

        if (searchRequest.getCompanyName() != null) {
            Specification<Employer> companySpec = EmployerSpecification.hasCompanyNameLike(searchRequest.getCompanyName());
            spec = (spec == null) ? companySpec : spec.and(companySpec);
        }

        if (searchRequest.getIsApproved() != null) {
            Specification<Employer> approvalSpec = EmployerSpecification.hasApprovalStatus(searchRequest.getIsApproved());
            spec = (spec == null) ? approvalSpec : spec.and(approvalSpec);
        }

        List<Employer> employers = (spec == null) ? employerRepository.findAll() : employerRepository.findAll(spec);

        return employers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void approveEmployer(String employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기업이 존재하지 않습니다: " + employerId));

        employer.setIsApproved(AgreementStatus.Y);
        employerRepository.save(employer);

        sendApprovalEmail(employer); // 이메일 발송
    }

    private AdminEmployerRequestDto convertToDto(Employer employer) {
        return AdminEmployerRequestDto.builder()
                .employerId(employer.getEmployerId())
                .companyName(employer.getCompanyName())
                .bizRegNo(employer.getBizRegNo())
                .bizRegistrationUrl(employer.getBizRegistrationUrl())
                .companyEmail(employer.getCompanyEmail())
                .createdAt(employer.getCreatedAt())
                .isApproved(employer.getIsApproved())
                .build();
    }

    private void sendApprovalEmail(Employer employer) {
        String toEmail = employer.getCompanyEmail();
        String subject = "기업 승인 완료 안내";
        String url = "http://localhost:3000/emp/info?employerId=" + employer.getEmployerId(); // 운영시 https로 변경

        Context context = new Context();
        context.setVariable("companyName", employer.getCompanyName());
        context.setVariable("signupUrl", url);

        mailService.sendHtmlMail(toEmail, subject, "employer-approval", context);
    }

    @Override
    public ServiceResult getAllMenus() {
        List<Menu> menus = menuRepository.findAll();
        return ServiceResult.success(SuccessCode.OK, MenuDto.listOf(menus));
    }

    /**
     * 한 번에 메뉴 리스트를 INSERT/UPDATE 처리합니다.
     * 기존 엔티티는 Managed 상태로 업데이트, 신규만 saveAll 호출
     */
    @Override
    @Transactional
    public ServiceResult saveMenus(List<MenuDto> menuDtos) {
        List<Menu> toCreate = new ArrayList<>();
        List<Menu> toUpdate = new ArrayList<>();

        // 분리: 존재하는 ID는 업데이트, 그렇지 않으면 생성
        for (MenuDto dto : menuDtos) {
            Integer id = dto.getMenuId();
            if (id != null && menuRepository.existsById(id)) {
                // 업데이트 대상
                Menu existing = menuRepository.getReferenceById(id);
                existing.setMenuName(dto.getMenuName());
                existing.setMenuPath(dto.getMenuPath());
                existing.setLevel(dto.getLevel());
                existing.setDisplayOrder(dto.getDisplayOrder());
                existing.setIsActive(dto.getIsActive());
                existing.setAccessRole(dto.getAccessRole());
                existing.setIcon(dto.getIcon());
                // 부모 설정 (없으면 null)
                if (dto.getParentId() != null && menuRepository.existsById(dto.getParentId())) {
                    Menu parentRef = menuRepository.getReferenceById(dto.getParentId());
                    existing.setParent(parentRef);
                } else {
                    existing.setParent(null);
                }
                toUpdate.add(existing);
            } else {
                // 신규 생성 대상 (ID가 null이거나 DB에 없음)
                Menu newMenu = Menu.builder()
                        .menuName(dto.getMenuName())
                        .menuPath(dto.getMenuPath())
                        .level(dto.getLevel())
                        .displayOrder(dto.getDisplayOrder())
                        .isActive(dto.getIsActive())
                        .accessRole(dto.getAccessRole())
                        .icon(dto.getIcon())
                        .build();
                if (dto.getParentId() != null && menuRepository.existsById(dto.getParentId())) {
                    Menu parentRef = menuRepository.getReferenceById(dto.getParentId());
                    newMenu.setParent(parentRef);
                }
                toCreate.add(newMenu);
            }
        }

        // 신규만 저장 (INSERT)
        List<Menu> created = toCreate.isEmpty() ? Collections.emptyList() : menuRepository.saveAll(toCreate);
        // 업데이트는 엔티티 매니저에 의해 자동 반영 (UPDATE)

        // 결과 조합
        List<Menu> resultEntities = new ArrayList<>();
        resultEntities.addAll(toUpdate);
        resultEntities.addAll(created);

        // DTO 변환
        List<MenuDto> resultDtos = resultEntities.stream()
                .map(MenuDto::of)
                .collect(Collectors.toList());

        return ServiceResult.success(SuccessCode.UPDATED, resultDtos);
    }
}
