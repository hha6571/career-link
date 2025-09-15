package com.career.careerlink.admin.commonCode.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
public class CommonCodeDto {
    private String id;
    @NotBlank
    private String groupCode;
    @NotBlank
    private String code;
    private String codeName;
    private String parentCode;
    private Integer sortOrder;
    private Integer level;
    private String useYn;
}

