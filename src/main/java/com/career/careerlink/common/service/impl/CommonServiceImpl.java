package com.career.careerlink.common.service.impl;

import com.career.careerlink.admin.dto.CommonCodeDto;
import com.career.careerlink.admin.dto.MenuDto;
import com.career.careerlink.admin.entity.Menu;
import com.career.careerlink.admin.mapper.CommonCodeMapper;
import com.career.careerlink.common.dto.FaqDto;
import com.career.careerlink.common.dto.NoticeDetailDto;
import com.career.careerlink.common.dto.NoticeDto;
import com.career.careerlink.common.dto.NoticeRequestDto;
import com.career.careerlink.common.entity.Faq;
import com.career.careerlink.common.entity.Notice;
import com.career.careerlink.common.entity.enums.Category;
import com.career.careerlink.common.mapper.NoticeMapper;
import com.career.careerlink.common.repository.FaqRepository;
import com.career.careerlink.common.repository.NoticeDetailRepository;
import com.career.careerlink.common.service.CommonService;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.career.careerlink.admin.repository.MenuRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MenuRepository menuRepository;
    private final CommonCodeMapper commonCodeMapper;
    private final NoticeMapper noticeMapper;
    private final NoticeDetailRepository noticeDetailRepository;
    private final FaqRepository faqReposotory;

    @Override
    public List<MenuDto> getAllMenus(String accessToken) {
        String userRole = jwtTokenProvider.getRole(accessToken.replace("Bearer ",""));
        List<Menu> menus = menuRepository.findByAccessRoleAndIsActiveOrderByDisplayOrderAscMenuIdAsc(userRole,"Y");
        return MenuDto.listOf(menus);
    }

    @Override
    public List<CommonCodeDto> getCommonCodes(String groupCode) {
        return commonCodeMapper.getCommonCodes(groupCode);
    }

    @Override
    public List<CommonCodeDto> allCodesByGroup(String groupCode) {
        if (!StringUtils.hasText(groupCode)) {
            throw new IllegalArgumentException("groupCode is required");
        }
        return commonCodeMapper.allCodesByGroup(groupCode);
    }

    @Override
    public List<CommonCodeDto> parentsByGroup(String groupCode) {
        if (!StringUtils.hasText(groupCode)) {
            throw new IllegalArgumentException("groupCode is required");
        }
        return commonCodeMapper.parentsByGroup(groupCode);
    }

    @Override
    public List<CommonCodeDto> childrenByParent(String groupCode, String parentCode) {
        if (!StringUtils.hasText(groupCode)) {
            throw new IllegalArgumentException("groupCode is required");
        }
        if (!StringUtils.hasText(parentCode)) {
            throw new IllegalArgumentException("parentCode is required");
        }
        return commonCodeMapper.childrenByParent(groupCode, parentCode);
    }

    @Override
    public Page<NoticeDto> getCommonNotices(NoticeRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        List<NoticeDto> rows = noticeMapper.getCommonNotices(req, offset, safeSize);
        long total = noticeMapper.commonNoticeCount(req);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public NoticeDetailDto getNotice(Long noticeId) {
        Notice notice = noticeDetailRepository.findById(noticeId)
                .orElseThrow(() -> new CareerLinkException("공지사항 내용이 없습니다."));
        notice.increaseViewCount();
        return NoticeDetailDto.of(notice);
    }

    @Override
    public List<FaqDto> getFaqs(Category category) {
        List<Faq> faqs = (category == null)
                ? faqReposotory.findAll()
                : faqReposotory.findByCategory(category);
        return FaqDto.listOf(faqs);
    }
}
