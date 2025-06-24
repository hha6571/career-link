package com.career.careerlink.applicant.account.controller;

import com.career.careerlink.applicant.account.dto.AccountRequestPassWordDto;
import com.career.careerlink.applicant.account.service.AccountService;
import com.career.careerlink.users.dto.ApplicantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 계정관리
     */
    @GetMapping("/account/getProfile")
    public ApplicantDto getProfile() {
        return accountService.getProfile();
    }

    @PutMapping("/account/updateProfile")
    public ApplicantDto updateProfile(@RequestBody ApplicantDto dto) {
        return accountService.updateProfile(dto);
    }

    @PostMapping("/account/changePassword")
    public void changePassword(@RequestBody AccountRequestPassWordDto requestPassWordDto) {
        accountService.changePassword(requestPassWordDto);
    }

    @PostMapping("/account/withdraw")
    public void withdraw() {
        accountService.withdraw();
    }

}
