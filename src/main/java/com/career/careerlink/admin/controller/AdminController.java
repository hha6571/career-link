package com.career.careerlink.admin.controller;

import com.career.careerlink.admin.dto.*;
import com.career.careerlink.admin.service.AdminService;
import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.job.service.JobPostingService;
import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import com.career.careerlink.faq.entity.enums.Category;
import com.career.careerlink.global.response.SkipWrap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JobPostingService jobPostingService;

    @GetMapping("/emp/requests")
    public List<AdminEmployerRequestDto> getAllEmployers(@ModelAttribute AdminEmployerRequestDto searchRequest) {
        return adminService.getAllEmployersWithFilter(searchRequest);
    }

    @PostMapping("/emp/{employerId}/approve")
    public void approveEmployer(@PathVariable String employerId) {
        adminService.approveEmployer(employerId);
    }

    /**메뉴관리**/
    @GetMapping("/menu")
    public List<MenuDto> getAllMenus(@RequestParam String accessRole) {
        return adminService.getAllMenus(accessRole);
    }

    @PostMapping("/saveMenus")
    public void saveMenus(@RequestBody MenuDto saveDto) {
        adminService.saveMenus(saveDto);
    }
    /**공통코드관리**/
    @GetMapping("/getParentCodes")
    public Page<CommonCodeDto> getParentCodes(@ModelAttribute CommonCodeSearchRequest req) {
        return adminService.getParentCodes(req);
    }

    @GetMapping("/getChildCodes")
    public Page<CommonCodeDto> getChildCodes(@ModelAttribute CommonCodeSearchRequest req) {
        return adminService.getChildCodes(req);
    }

    @PostMapping("/saveCommonCodes")
    public void saveCommonCodes(@RequestBody CommonCodeSaveDto saveDto) {
        adminService.saveCommonCodes(saveDto);
    }
    /**
     * 사용자관리
     **/
    @GetMapping("/getUsers")
    public Page<UsersDto> getUsers(@ModelAttribute UsersRequestDto req){
        return adminService.getUsers(req);
    }
    @PostMapping("/saveUsers")
    public void saveUsers(@RequestBody List<UsersDto> list){
        adminService.saveUsers(list);
    }
    /**
     * 공지사항관리
     **/
    @GetMapping("/getNotices")
    public Page<NoticeDto> getNotices(NoticeRequestDto req) {
        return adminService.getNotices(req);
    }
    @SkipWrap
    @PostMapping(value = "/saveNotice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long createNotice(
            @RequestPart("dto") NoticeDetailDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return adminService.createNotice(dto, file);
    }
    @SkipWrap
    @PutMapping(value = "/saveNotice/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long updateNotice(
            @RequestPart("dto") NoticeDetailDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return adminService.updateNotice(dto, file);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deleteNotice/{id}")
    public void deleteNotice(@PathVariable Long id) {
       adminService.deleteNotice(id);
    }
    /**
     * 자주하는질문
     **/
    @GetMapping("/getFaqs")
    public List<FaqDto> getFaqs(@RequestParam Category category) {
        return adminService.getFaqs(category);
    }
    @PostMapping("/createFaq")
    public void createFaq(@RequestBody FaqDto dto) {
        adminService.createFaq(dto);
    }
    @PutMapping("/updateFaq")
    public void updateFaq(@RequestBody FaqDto dto) {
        adminService.updateFaq(dto);
    }
    @DeleteMapping("/deleteFaq/{faqId}")
    public void deleteFaq(@PathVariable Long faqId) {
        adminService.deleteFaq(faqId);
    }

    /**
     * 기업공고 리스트 조회
     */
    @GetMapping("/job-postings/manage")
    public Page<AdminJobPostingResponse> getJobPostingList(AdminJobPostingSearchRequest req) {
        return jobPostingService.searchForAdmin(req);
    }

    /**
     * 기업공고 삭제처리 (다건)
     */
    @SkipWrap
    @PostMapping("/job-postings/delete-bulk")
    public int jobPostingDeleteBulk(@RequestBody List<String> targetJobPostingIds) {
        return jobPostingService.deleteBulkByAdmin(targetJobPostingIds);
    }
}
