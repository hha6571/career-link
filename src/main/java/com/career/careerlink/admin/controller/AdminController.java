package com.career.careerlink.admin.controller;

import com.career.careerlink.admin.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.dto.MenuDto;
import com.career.careerlink.admin.service.AdminService;
import com.career.careerlink.common.controller.BaseController;
import com.career.careerlink.common.response.ResponseResult;
import com.career.careerlink.common.response.ServiceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController extends BaseController {

    private final AdminService adminService;

    @GetMapping("/emp/requests")
    public List<AdminEmployerRequestDto> getAllEmployers(@ModelAttribute AdminEmployerRequestDto searchRequest) {
        return adminService.getAllEmployersWithFilter(searchRequest);
    }

    @PostMapping("/emp/{employerId}/approve")
    public void approveEmployer(@PathVariable String employerId) {
        adminService.approveEmployer(employerId);
    }

    @GetMapping("/menu/list")
    public ResponseEntity<ResponseResult> getAllMenus(){
        ServiceResult serviceResult = adminService.getAllMenus();
        return result(serviceResult);
    }

    @PostMapping("/menu/save")
    public ResponseEntity<ResponseResult> saveMenus(@RequestBody List<MenuDto> menuDtos) {
        ServiceResult serviceResult = adminService.saveMenus(menuDtos);
        return result(serviceResult);
    }
}
