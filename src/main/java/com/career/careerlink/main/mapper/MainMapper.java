package com.career.careerlink.main.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MainMapper {

    List<Map<String, Object>> selectMainJobs();
    List<Map<String, Object>> selectMainEmployers();
}
