package com.career.careerlink.admin.user.mapper;

import com.career.careerlink.admin.user.dto.AdminEmployerRequestDto;
import com.career.careerlink.admin.user.dto.AdminEmployersSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployerManageMapper {
    long employersCount(
            @Param("req") AdminEmployersSearchRequest req
    );

    List<AdminEmployerRequestDto> getEmployers(
            @Param("req") AdminEmployersSearchRequest req,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort,
            @Param("direction") String direction
    );

    // 일괄 승인 (N -> Y)
    int approveIfPendingBulk(@Param("targetEmployerIds") List<String> targetEmployerIds, String adminUserId);
}
