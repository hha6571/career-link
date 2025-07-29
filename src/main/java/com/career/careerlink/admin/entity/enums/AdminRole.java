package com.career.careerlink.admin.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AdminRole {
    ADMIN, MANAGER
}
