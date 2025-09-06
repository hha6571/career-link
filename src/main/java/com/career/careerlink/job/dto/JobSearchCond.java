package com.career.careerlink.job.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JobSearchCond {
    String keyword;    // 제목/본문 like 검색
    String jobField;   // job_field_code
    String location;   // location_code
    String empType;    // employment_type_code
    String edu;        // education_level_code
    String exp;        // career_level_code
    String sal;        // salary_code
}
