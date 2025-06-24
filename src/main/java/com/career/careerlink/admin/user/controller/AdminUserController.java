package com.career.careerlink.admin.user.controller;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.UsersDto;
import com.career.careerlink.admin.user.dto.UsersRequestDto;
import com.career.careerlink.admin.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    public List<AdminEmployerRequestDto> getAllEmployers(@ModelAttribute AdminEmployerRequestDto searchRequest) {
        return adminUserService.getAllEmployersWithFilter(searchRequest);
    }

    /**
     * 기업등록 승인
     */
    @PostMapping("/emp/{employerId}/approve")
    public void approveEmployer(@PathVariable String employerId) {
        adminUserService.approveEmployer(employerId);
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
