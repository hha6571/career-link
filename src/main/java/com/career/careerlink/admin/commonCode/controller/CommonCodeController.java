package com.career.careerlink.admin.commonCode.controller;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSaveDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSearchRequest;
import com.career.careerlink.admin.commonCode.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/commonCode")
@RequiredArgsConstructor
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    /**공통코드관리**/
    @GetMapping("/getParentCodes")
    public Page<CommonCodeDto> getParentCodes(@ModelAttribute CommonCodeSearchRequest req) {
        return commonCodeService.getParentCodes(req);
    }

    @GetMapping("/getChildCodes")
    public Page<CommonCodeDto> getChildCodes(@ModelAttribute CommonCodeSearchRequest req) {
        return commonCodeService.getChildCodes(req);
    }

    @PostMapping("/saveCommonCodes")
    public void saveCommonCodes(@RequestBody CommonCodeSaveDto saveDto) {
        commonCodeService.saveCommonCodes(saveDto);
    }
}
