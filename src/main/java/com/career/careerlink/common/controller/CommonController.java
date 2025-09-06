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

    /** 전체(부모+자식) - 프런트 fetch(`/common/common-codes?group=...`) */
    @GetMapping("/common-codes")
    public List<CommonCodeDto> allCodes(@RequestParam("group") String group) {
        return commonService.allCodesByGroup(group);
    }
    /**
     * 그룹의 대분류(부모, parent_code IS NULL)만 반환
     * GET /api/common-codes/parents?groupCode=JOB_FIELD
     */
    @GetMapping("/parents")
    public List<CommonCodeDto> parentsByGroup(@RequestParam String groupCode) {
        return commonService.parentsByGroup(groupCode);
    }

    /**
     * 특정 부모 하위(자식) 코드만 반환
     * GET /api/common-codes/children?groupCode=JOB_FIELD&parentCode=DEV
     */
    @GetMapping("/children")
    public List<CommonCodeDto> childrenByParent(
            @RequestParam String groupCode,
            @RequestParam String parentCode
    ) {
        return commonService.childrenByParent(groupCode, parentCode);
    }
}
