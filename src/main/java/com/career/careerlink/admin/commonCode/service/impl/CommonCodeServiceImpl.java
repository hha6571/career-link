package com.career.careerlink.admin.commonCode.service.impl;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSaveDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSearchRequest;
import com.career.careerlink.admin.commonCode.mapper.CommonCodeMapper;
import com.career.careerlink.admin.commonCode.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;

    private static <T> List<T> nvl(List<T> v) { return v == null ? List.of() : v; }

    @Override
    public Page<CommonCodeDto> getParentCodes(CommonCodeSearchRequest req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = commonCodeMapper.parentCodesCount(req);
        List<CommonCodeDto> rows = commonCodeMapper.parentCodes(req, offset, safeSize);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
    }

    @Override
    public Page<CommonCodeDto> getChildCodes(CommonCodeSearchRequest req) {
        int page = Optional.ofNullable(req.getPage()).orElse(0);
        int size = Optional.ofNullable(req.getSize()).orElse(10);

        // 0-based page → offset
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        long total = commonCodeMapper.childCodesCount(req);
        List<CommonCodeDto> rows = commonCodeMapper.childCodes(req, offset, safeSize);

        return new PageImpl<>(rows, PageRequest.of(safePage, safeSize), total);
        //(실제데이터,페이지정보,전체데이터수) => 이 세가지 정보로 페이징 정보 자동생성 Spring Data 클래스
    }

    @Override
    public void saveCommonCodes(CommonCodeSaveDto saveDto, String AdminUserId) {
        var pI = nvl(saveDto.getParentInserts());
        var pU = nvl(saveDto.getParentUpdates());
        var pD = nvl(saveDto.getParentDeletes());
        var cI = nvl(saveDto.getChildInserts());
        var cU = nvl(saveDto.getChildUpdates());
        var cD = nvl(saveDto.getChildDeletes());

        // 1) 삭제: 자식 → 부모
        if (!cD.isEmpty()) commonCodeMapper.deleteChildren(cD);
        if (!pD.isEmpty()) commonCodeMapper.deleteParents(pD);

        // 2) 삽입: 부모 → 자식
        if (!pI.isEmpty()) commonCodeMapper.insertParents(pI,AdminUserId);
        if (!cI.isEmpty()) commonCodeMapper.insertChildren(cI,AdminUserId);

        // 3) 수정: 부모 → 자식 (단건씩)
        if (!pU.isEmpty()) {
            for (var dto : pU)commonCodeMapper.updateParents(dto, AdminUserId);

        }
        if (!cU.isEmpty()) {
            for (var dto : cU) commonCodeMapper.updateChildren(dto,AdminUserId);
        }
    }

}
