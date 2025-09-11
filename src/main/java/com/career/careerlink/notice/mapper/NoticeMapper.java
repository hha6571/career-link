package com.career.careerlink.notice.mapper;

import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    long getNoticeCount(NoticeRequestDto req);
    List<NoticeDto> getAdminNotices(@Param("req")NoticeRequestDto req,
                               @Param("offset") int offset,
                               @Param("limit") int limit);
    int commonNoticeCount(NoticeRequestDto req);
    List<NoticeDto> getCommonNotices(
            @Param("req") NoticeRequestDto req,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
}
