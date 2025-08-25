package com.career.careerlink.admin.service.impl;

import com.career.careerlink.admin.dto.*;
import com.career.careerlink.admin.entity.Menu;
import com.career.careerlink.admin.mapper.CommonCodeMapper;
import com.career.careerlink.admin.repository.AdminRepository;
import com.career.careerlink.admin.repository.MenuRepository;
import com.career.careerlink.admin.service.AdminService;
import com.career.careerlink.admin.spec.EmployerSpecification;
import com.career.careerlink.common.send.MailService;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.security.JwtTokenProvider;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final EmployerRepository employerRepository;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MenuRepository menuRepository;
    private final CommonCodeMapper commonCodeMapper;

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
        String subject = "[CareerLinl] 기업 승인 완료 안내";
        String url = "http://localhost:3000/emp/info?employerId=" + employer.getEmployerId(); // 운영시 https로 변경

        Context context = new Context();
        context.setVariable("companyName", employer.getCompanyName());
        context.setVariable("signupUrl", url);

        mailService.sendHtmlMail(toEmail, subject, "employer-approval", context);
    }

    @Override
    public List<MenuDto> getAllMenus() {
        List<Menu> menus = menuRepository.findAll();
        if(menus.isEmpty() ){
            //커스텀에러 필요시
            throw new CareerLinkException("조회된데이터가 없습니다.");
        }
        return MenuDto.listOf(menus);
    }

    @Override
    public List<CommonCodeDto> getCommonCodes(String groupCode) {
        return commonCodeMapper.getCommonCodes(groupCode);
    }

    @Override
    public Page<CommonCodeDto> getParentCodes(CommonCodeSearchRequest req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = commonCodeMapper.parentCodesCount(req);
        List<CommonCodeDto> rows = commonCodeMapper.parentCodes(req, offset, safeSize);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    public Page<CommonCodeDto> getChildCodes(CommonCodeSearchRequest req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = commonCodeMapper.childCodesCount(req);
        List<CommonCodeDto> rows = commonCodeMapper.childCodes(req, offset, safeSize);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
        //(실제데이터,페이지정보,전체데이터수) => 이 세가지 정보로 페이징 정보 자동생성 Spring Data 클래스
    }

    @Override
    public void saveCommonCodes(CommonCodeSaveDto saveDto) {
        var pI = nvl(saveDto.getParentInserts());
        var pU = nvl(saveDto.getParentUpdates());
        var pD = nvl(saveDto.getParentDeletes());
        var cI = nvl(saveDto.getChildInserts());
        var cU = nvl(saveDto.getChildUpdates());
        var cD = nvl(saveDto.getChildDeletes());

        // 1) 삭제: 자식 → 부모
        if (!cD.isEmpty()) commonCodeMapper.deleteChildren(cD);
        if (!pD.isEmpty()) commonCodeMapper.deleteParents(pD);

        // 2) 삽입: 부모 → 자식
        if (!pI.isEmpty()) commonCodeMapper.insertParents(pI);
        if (!cI.isEmpty()) commonCodeMapper.insertChildren(cI);

        // 3) 수정: 부모 → 자식 (단건씩)
        if (!pU.isEmpty()) {
            for (var dto : pU) commonCodeMapper.updateParents(dto);

        }
        if (!cU.isEmpty()) {
            for (var dto : cU) commonCodeMapper.updateChildren(dto);
        }
    }
    private static <T> List<T> nvl(List<T> v) { return v == null ? List.of() : v; }

}
