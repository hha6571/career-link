package com.career.careerlink.job.dto;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class JobFiltersResponse {
    List<CommonCodeDto> jobFields;
    List<CommonCodeDto> locations;
    List<CommonCodeDto> employmentTypes;
    List<CommonCodeDto> educationLevels;
    List<CommonCodeDto> careerLevels;
    List<CommonCodeDto> salary;
}