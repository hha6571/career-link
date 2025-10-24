package com.career.careerlink.admin.user.controller;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.AdminEmployersSearchRequest;
import com.career.careerlink.admin.user.dto.UsersDto;
import com.career.careerlink.admin.user.dto.UsersRequestDto;
import com.career.careerlink.admin.user.service.AdminUserService;
import com.career.careerlink.global.response.SkipWrap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 기업 목록
     */
    @GetMapping("/emp/requests")
    public Page<AdminEmployerRequestDto> getEmployers(@ModelAttribute AdminEmployersSearchRequest searchRequest) {
        return adminUserService.getEmployers(searchRequest);
    }

    /**
     * 기업등록 승인
     */
    @PostMapping("/emp/{employerId}/approve")
    public void approveEmployer(@PathVariable String employerId, Principal principal) {
        String adminUserId = (principal != null) ? principal.getName() : null;
        adminUserService.approveEmployer(employerId, adminUserId);
    }

    /**
     * 기업등록 승인(다건)
     */
    @SkipWrap
    @PostMapping("/emp/approve-bulk")
    public int approveEmployerBulk(@RequestBody List<String> targetEmployerIds, Principal principal) {
        String adminUserId = (principal != null) ? principal.getName() : null;
        return adminUserService.approveEmployerBulk(targetEmployerIds, adminUserId);
    }

    /**
     * 사용자관리
     **/
    @GetMapping("/applicant/getUsers")
    public Page<UsersDto> getUsers(@ModelAttribute UsersRequestDto req){
        return adminUserService.getUsers(req);
    }
    
    @PostMapping("/applicant/saveUsers")
    public void saveUsers(@RequestBody List<UsersDto> list){
        adminUserService.saveUsers(list);
    }
}
