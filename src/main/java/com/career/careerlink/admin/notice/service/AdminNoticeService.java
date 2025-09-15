package com.career.careerlink.admin.notice.service;

import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AdminNoticeService {
    Page<NoticeDto> getNotices(NoticeRequestDto req);
    Long updateNotice(NoticeDetailDto dto, MultipartFile file);
    Long createNotice(NoticeDetailDto dto, MultipartFile file);
    void deleteNotice(Long id);
}
