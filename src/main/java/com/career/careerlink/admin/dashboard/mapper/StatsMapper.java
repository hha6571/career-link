package com.career.careerlink.admin.dashboard.mapper;

import com.career.careerlink.admin.dashboard.dto.PointDto;
import com.career.careerlink.admin.dashboard.entity.enums.Granularity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StatsMapper {

    List<PointDto> countPostings(
            @Param("granularity") Granularity granularity,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime toExclusive,
            @Param("pattern") String pattern
    );

    List<PointDto> countApplicants(
            @Param("granularity") Granularity granularity,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime toExclusive,
            @Param("pattern") String pattern
    );
}