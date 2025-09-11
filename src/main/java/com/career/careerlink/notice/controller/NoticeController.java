package com.career.careerlink.notice.controller;

import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import com.career.careerlink.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/getNotices")
    public Page<NoticeDto> getNotices(NoticeRequestDto req) {
        return noticeService.getCommonNotices(req);
    }

    @GetMapping("/getNotice/{id}")
    public NoticeDetailDto getNotice(@PathVariable("id") Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

}
