package com.career.careerlink.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {
    private String x; // 그룹핑된 값 (YYYY-MM-DD, YYYY-MM, YYYY)
    private Long y;   // 건수
}