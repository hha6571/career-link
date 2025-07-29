package com.career.careerlink.admin.controller;

import com.career.careerlink.admin.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/emp/requests")
    public List<AdminEmployerRequestDto> getAllEmployers(@ModelAttribute AdminEmployerRequestDto searchRequest) {
        return adminService.getAllEmployersWithFilter(searchRequest);
    }

    @PostMapping("/emp/{employerId}/approve")
    public void approveEmployer(@PathVariable String employerId) {
        adminService.approveEmployer(employerId);
    }
}
