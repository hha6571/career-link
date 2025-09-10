package com.career.careerlink.common.service;

import com.career.careerlink.admin.dto.CommonCodeDto;
import com.career.careerlink.admin.dto.MenuDto;
import com.career.careerlink.common.dto.FaqDto;
import com.career.careerlink.common.dto.NoticeDetailDto;
import com.career.careerlink.common.dto.NoticeDto;
import com.career.careerlink.common.dto.NoticeRequestDto;
import com.career.careerlink.common.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface CommonService {
    List<MenuDto> getAllMenus(@RequestHeader("Authorization") String accessToken);
    List<CommonCodeDto> getCommonCodes(String groupCode);
    List<CommonCodeDto> allCodesByGroup(String groupCode);
    List<CommonCodeDto> parentsByGroup(String groupCode);
    List<CommonCodeDto> childrenByParent(String groupCode, String parentCode);
    Page<NoticeDto> getCommonNotices(NoticeRequestDto req);
    NoticeDetailDto getNotice(Long noticeId);
    List<FaqDto> getFaqs(Category category);
    }
