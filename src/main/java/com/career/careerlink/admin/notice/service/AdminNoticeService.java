package com.career.careerlink.admin.notice.service;

import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AdminNoticeService {
    Page<NoticeDto> getNotices(NoticeRequestDto req);
    Integer createNotice(NoticeDetailDto dto, MultipartFile thumbnailFile, MultipartFile attachmentFile);
    Integer updateNotice(Integer id, NoticeDetailDto dto, MultipartFile thumbnailFile, MultipartFile attachmentFile);
    void deleteNotice(Integer id);
}
