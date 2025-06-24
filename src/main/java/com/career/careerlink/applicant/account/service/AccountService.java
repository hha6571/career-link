package com.career.careerlink.applicant.account.service;
import com.career.careerlink.applicant.account.dto.AccountRequestPassWordDto;
import com.career.careerlink.users.dto.ApplicantDto;

public interface AccountService {
    ApplicantDto getProfile();
    ApplicantDto updateProfile(ApplicantDto dto);
    void changePassword(AccountRequestPassWordDto requestPassWordDto);
    void withdraw();
}
