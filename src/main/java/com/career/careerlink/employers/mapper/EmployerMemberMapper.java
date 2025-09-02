package com.career.careerlink.employers.mapper;

import com.career.careerlink.employers.dto.EmployerMemberDto;
import com.career.careerlink.employers.dto.EmployerMemberSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployerMemberMapper {

    long membersCount(
            @Param("req") EmployerMemberSearchRequest req,
            @Param("employerUserId") String employerUserId
    );

    List<EmployerMemberDto> members(
            @Param("req") EmployerMemberSearchRequest req,
            @Param("employerUserId") String employerUserId,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort,
            @Param("direction") String direction
    );

    // 단건 승인 (N -> Y 일 때만)
    int approveIfPending(@Param("targetEmployerUserId") String targetEmployerUserId,
                         @Param("employerUserId") String employerUserId);

    // 일괄 승인 (N -> Y)
    int approveIfPendingBulk(@Param("targetEmployerUserIds") List<String> targetEmployerUserIds,
                             @Param("employerUserId") String employerUserId);
}
