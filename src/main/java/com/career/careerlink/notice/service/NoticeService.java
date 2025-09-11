package com.career.careerlink.notice.service;

import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import org.springframework.data.domain.Page;

public interface NoticeService {
    Page<NoticeDto> getCommonNotices(NoticeRequestDto req);
    NoticeDetailDto getNotice(Long noticeId);
    }
