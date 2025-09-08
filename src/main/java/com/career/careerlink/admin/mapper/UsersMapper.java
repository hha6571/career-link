package com.career.careerlink.admin.mapper;

import com.career.careerlink.admin.dto.UsersDto;
import com.career.careerlink.admin.dto.UsersRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UsersMapper {
    long usersCount(UsersRequestDto req);
    List<UsersDto> getUsers(@Param("req") UsersRequestDto req,
                            @Param("offset") int offset,
                            @Param("limit") int limit);
    void updateEmployerStatus(@Param("userPk") String userPk,
                             @Param("userStatus") String userStatus);

    void updateApplicantStatus(@Param("userPk") String userPk,
                              @Param("userStatus") String userStatus);
}
