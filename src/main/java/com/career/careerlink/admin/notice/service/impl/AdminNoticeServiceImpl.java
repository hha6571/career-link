package com.career.careerlink.admin.notice.service.impl;

import com.career.careerlink.admin.notice.service.AdminNoticeService;
import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.global.s3.S3Service;
import com.career.careerlink.global.s3.S3UploadType;
import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import com.career.careerlink.notice.entity.Notice;
import com.career.careerlink.notice.mapper.NoticeMapper;
import com.career.careerlink.notice.repository.NoticeDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeDetailRepository noticeDetailRepository;
    private final S3Service s3Service;

    @Override
    public Page<NoticeDto> getNotices(NoticeRequestDto req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;
        long total = noticeMapper.getNoticeCount(req);
        List<NoticeDto> rows = noticeMapper.getAdminNotices(req, offset, safeSize);
        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    @Transactional
    public Integer createNotice(NoticeDetailDto dto, MultipartFile thumbnailFile, MultipartFile attachmentFile) {
        String thumbnailUrl = null;
        String attachmentUrl = null;

        // 썸네일 업로드
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = s3Service.uploadFile(S3UploadType.NOTICE_FILE, thumbnailFile);
        }

        // 첨부파일 업로드
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            attachmentUrl = s3Service.uploadFile(S3UploadType.NOTICE_FILE, attachmentFile);
        }

        Notice notice = dto.toEntity(thumbnailUrl, attachmentUrl);
        Notice saved = noticeDetailRepository.save(notice);
        return saved.getNoticeId();
    }

    @Override
    @Transactional
    public Integer updateNotice(Integer id, NoticeDetailDto dto, MultipartFile thumbnailFile, MultipartFile attachmentFile) {
        Notice notice = noticeDetailRepository.findById(id)
                .orElseThrow(() -> new CareerLinkException("공지사항이 존재하지 않습니다."));

        String thumbnailUrl = notice.getThumbnailUrl();
        String attachmentUrl = notice.getAttachmentUrl();

        // 썸네일 교체
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailUrl = s3Service.uploadFile(S3UploadType.NOTICE_FILE, thumbnailFile);
            if (notice.getThumbnailUrl() != null) {
                s3Service.deleteFileByUrl(notice.getThumbnailUrl());
            }
        }

        // 첨부파일 교체
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            attachmentUrl = s3Service.uploadFile(S3UploadType.NOTICE_FILE, attachmentFile);
            if (notice.getAttachmentUrl() != null) {
                s3Service.deleteFileByUrl(notice.getAttachmentUrl());
            }
        }

        dto.updateEntity(notice, thumbnailUrl, attachmentUrl);
        Notice updated = noticeDetailRepository.save(notice);
        return updated.getNoticeId();
    }

    @Override
    @Transactional
    public void deleteNotice(Integer id) {
        Notice notice = noticeDetailRepository.findById(id)
                .orElseThrow(() -> new CareerLinkException("공지사항이 존재하지 않습니다."));
        notice.softDelete();
    }
}
