package com.career.careerlink.job.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobSearchCond {
    private String keyword;    // 제목/본문 like 검색
    private List<String> jobField;   // job_field_code
    private List<String> location;   // location_code
    private List<String> empType;    // employment_type_code
    private List<String> edu;        // education_level_code
    private List<String> exp;        // career_level_code
    private List<String> sal;        // salary_code
}
