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
    public Integer createNotice(
            @RequestPart("dto") NoticeDetailDto dto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "attachmentFile", required = false) MultipartFile attachmentFile
    ) {
        return adminNoticeService.createNotice(dto, thumbnailFile, attachmentFile);
    }

    @SkipWrap
    @PutMapping(value = "/saveNotice/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Integer updateNotice(
            @PathVariable Integer id,
            @RequestPart("dto") NoticeDetailDto dto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "attachmentFile", required = false) MultipartFile attachmentFile
    ) {
        return adminNoticeService.updateNotice(id, dto, thumbnailFile, attachmentFile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deleteNotice/{id}")
    public void deleteNotice(@PathVariable Integer id) {
        adminNoticeService.deleteNotice(id);
    }
}
