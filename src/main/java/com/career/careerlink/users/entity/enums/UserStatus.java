package com.career.careerlink.users.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserStatus {
    ACTIVE, DORMANT, WITHDRAWN
}
