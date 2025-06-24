package com.career.careerlink.admin.commonCode.service;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSaveDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSearchRequest;
import org.springframework.data.domain.Page;

public interface CommonCodeService {
    Page<CommonCodeDto> getParentCodes(CommonCodeSearchRequest req);
    Page<CommonCodeDto> getChildCodes(CommonCodeSearchRequest req);
    void saveCommonCodes(CommonCodeSaveDto saveDto, String AdminUserId);
}
