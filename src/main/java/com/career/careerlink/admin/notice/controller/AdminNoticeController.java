package com.career.careerlink.admin.notice.controller;

import com.career.careerlink.admin.notice.service.AdminNoticeService;
import com.career.careerlink.global.response.SkipWrap;
import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    /**
     * 공지사항관리
     **/
    @GetMapping("/getNotices")
    public Page<NoticeDto> getNotices(NoticeRequestDto req) {
        return adminNoticeService.getNotices(req);
    }

    @SkipWrap
    @PostMapping(value = "/saveNotice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long createNotice(
            @RequestPart("dto") NoticeDetailDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return adminNoticeService.createNotice(dto, file);
    }

    @SkipWrap
    @PutMapping(value = "/saveNotice/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long updateNotice(
            @RequestPart("dto") NoticeDetailDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return adminNoticeService.updateNotice(dto, file);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deleteNotice/{id}")
    public void deleteNotice(@PathVariable Long id) {
        adminNoticeService.deleteNotice(id);
    }
}
