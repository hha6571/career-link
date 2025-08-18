package com.career.careerlink.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommonCodeSaveDto {
    private List<CommonCodeDto> parentInserts;
    private List<CommonCodeDto> parentUpdates;
    private List<CommonCodeDto> parentDeletes;

    private List<CommonCodeDto> childInserts;
    private List<CommonCodeDto> childUpdates;
    private List<CommonCodeDto> childDeletes;
}
