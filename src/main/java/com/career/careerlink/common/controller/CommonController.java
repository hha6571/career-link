package com.career.careerlink.common.controller;

import com.career.careerlink.admin.dto.*;
import com.career.careerlink.common.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @GetMapping("/menus")
    public List<MenuDto> getAllMenus(@RequestHeader("Authorization") String accessToken) {
        return commonService.getAllMenus(accessToken);
    }

    @GetMapping("/getCommonCodes")
    public List<CommonCodeDto> getCommonCodes(@RequestParam String groupCode) {
        return commonService.getCommonCodes(groupCode);
    }

}
