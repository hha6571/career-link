package com.career.careerlink.notice.service.impl;

import com.career.careerlink.global.exception.CareerLinkException;
import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import com.career.careerlink.notice.entity.Notice;
import com.career.careerlink.notice.mapper.NoticeMapper;
import com.career.careerlink.notice.repository.NoticeDetailRepository;
import com.career.careerlink.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeDetailRepository noticeDetailRepository;

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

}
