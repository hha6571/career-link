package com.career.careerlink.applicant.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AgreementStatus {
    Y, N
}
