package com.career.careerlink.admin.service.impl;

import com.career.careerlink.admin.dto.*;
import com.career.careerlink.admin.entity.Menu;
import com.career.careerlink.admin.mapper.CommonCodeMapper;
import com.career.careerlink.admin.mapper.UsersMapper;
import com.career.careerlink.admin.repository.MenuRepository;
import com.career.careerlink.admin.service.AdminService;
import com.career.careerlink.admin.spec.EmployerSpecification;
import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import com.career.careerlink.faq.entity.Faq;
import com.career.careerlink.notice.entity.Notice;
import com.career.careerlink.faq.entity.enums.Category;
import com.career.careerlink.notice.mapper.NoticeMapper;
import com.career.careerlink.faq.repository.FaqRepository;
import com.career.careerlink.notice.repository.NoticeDetailRepository;
import com.career.careerlink.common.send.MailService;
import com.career.careerlink.employers.entity.Employer;
import com.career.careerlink.employers.repository.EmployerRepository;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.s3.S3Service;
import com.career.careerlink.global.s3.S3UploadType;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmployerRepository employerRepository;
    private final MailService mailService;
    private final MenuRepository menuRepository;
    private final CommonCodeMapper commonCodeMapper;
    private final UsersMapper usersMapper;
    private final NoticeMapper noticeMapper;
    private final NoticeDetailRepository noticeDetailRepository;
    private final S3Service s3Service;
    private final FaqRepository faqRepository;

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
        String url = "http://localhost:3000/emp/signup?employerId=" + employer.getEmployerId(); // 운영시 https로 변경

        Context context = new Context();
        context.setVariable("companyName", employer.getCompanyName());
        context.setVariable("signupUrl", url);

        mailService.sendHtmlMail(toEmail, subject, "employer-approval", context);
    }

    /**메뉴관리**/
    @Override
    public List<MenuDto> getAllMenus(String accessRole) {
        List<Menu> menus = menuRepository.findByAccessRoleOrderByDisplayOrderAscMenuIdAsc(accessRole);
        return MenuDto.listOf(menus);
    }

    @Override
    @Transactional
    public void saveMenus(MenuDto saveDto) {
        var deletes = nvl(saveDto.getDeletes());
        var inserts = nvl(saveDto.getInserts());
        var updates = nvl(saveDto.getUpdates());
        if (!deletes.isEmpty()) {
            menuRepository.deleteAllByIdInBatch(deletes);
        }

        // 2) 삽입
        for (MenuDto dto : inserts) {
            if (Integer.valueOf(2).equals(dto.getLevel()) && dto.getParentId() == null) {
                throw new CareerLinkException("하위 메뉴 저장 시 parentId는 필수입니다.");
            }

            Menu menu = new Menu();
            menu.setMenuName(dto.getMenuName());
            menu.setMenuPath(dto.getMenuPath());
            menu.setDisplayOrder(dto.getDisplayOrder());
            if (dto.getParentId() == null) {
                menu.setLevel(1);
                menu.setParent(null);
            } else {
                Menu parent = menuRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new CareerLinkException("부모 메뉴 없음: " + dto.getParentId()));
                menu.setParent(parent);
                menu.setLevel(2); // 혹은 parent.getLevel() + 1;
            }
            menu.setIsActive(dto.getIsActive());
            menu.setAccessRole(dto.getAccessRole());
            menu.setIcon(dto.getIcon());

            menuRepository.save(menu);
        }

        // 3) 수정
        for (MenuDto dto : updates) {
            Menu menu = menuRepository.findById(dto.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("수정 대상 메뉴 없음: " + dto.getMenuId()));

            menu.setMenuName(dto.getMenuName());
            menu.setMenuPath(dto.getMenuPath());
            menu.setDisplayOrder(dto.getDisplayOrder());
            if (dto.getParentId() == null) {
                menu.setParent(null);
                menu.setLevel(1);
            } else {
                Menu parent = menuRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("부모 메뉴 없음: " + dto.getParentId()));
                menu.setParent(parent);
                menu.setLevel(2);
            }
            menu.setIsActive(dto.getIsActive());
            menu.setAccessRole(dto.getAccessRole());
            menu.setIcon(dto.getIcon());
        }
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

    @Override
    public Page<UsersDto> getUsers(UsersRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

       // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = usersMapper.usersCount(req);
        List<UsersDto> rows = usersMapper.getUsers(req, offset, safeSize);
        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public void saveUsers(List<UsersDto> list) {
        for (UsersDto u : list) {
            if ("EMP".equals(u.getRole())) {
                usersMapper.updateEmployerStatus(u.getUserPk(), u.getUserStatus());
            } else if ("USER".equals(u.getRole())) {
                usersMapper.updateApplicantStatus(u.getUserPk(), u.getUserStatus());
            }
        }
    }
    @Override
    public Page<NoticeDto> getNotices(NoticeRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;
        long total = noticeMapper.getNoticeCount(req);
        List<NoticeDto> rows = noticeMapper.getAdminNotices(req, offset, safeSize);
        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize),total);
    }

    @Override
    @Transactional
    public Long createNotice(NoticeDetailDto dto, MultipartFile file) {
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = s3Service.uploadFile(S3UploadType.NOTICE_FILE, file);
        }

        Notice notice = dto.toEntity(fileUrl);
        Notice saved = noticeDetailRepository.save(notice);
        return saved.getNoticeId();
    }

    @Override
    @Transactional
    public Long updateNotice(NoticeDetailDto dto, MultipartFile file) {
        Notice notice = noticeDetailRepository.findById(dto.getNoticeId())
                .orElseThrow(() -> new CareerLinkException("공지사항이 존재하지 않습니다."));

        String fileUrl = notice.getFileUrl();
        if (file != null && !file.isEmpty()) {
            fileUrl = s3Service.uploadFile(S3UploadType.NOTICE_FILE, file);
            if (notice.getFileUrl() != null) {
                s3Service.deleteFileByUrl(notice.getFileUrl());
            }
        }

        dto.updateEntity(notice, fileUrl);
        Notice updated = noticeDetailRepository.save(notice);
        return updated.getNoticeId();
    }

    @Override
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeDetailRepository.findById(id)
                .orElseThrow(() -> new CareerLinkException("공지사항이 존재하지 않습니다."));
        notice.softDelete();
    }

    @Override
    public List<FaqDto> getFaqs(Category category) {
        List<Faq> faqs = faqRepository.findByCategory(category);
        return FaqDto.listOf(faqs);
    }

    @Override
    @Transactional
    public void createFaq(FaqDto dto) {
        Faq faq = dto.toEntity();
        faqRepository.save(faq);
    }

    @Override
    @Transactional
    public void updateFaq(FaqDto dto) {
        Faq faq = faqRepository.findById(dto.getFaqId().longValue())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 FAQ입니다."));
        dto.updateEntity(faq);
    }

    @Override
    @Transactional
    public void deleteFaq(Long faqId) {
        faqRepository.deleteById(faqId);
    }

    private static <T> List<T> nvl(List<T> v) { return v == null ? List.of() : v; }

}
