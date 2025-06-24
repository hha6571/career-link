package com.career.careerlink.applicant.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestPassWordDto {
    private String currentPassword;
    private String newPassword;
}
