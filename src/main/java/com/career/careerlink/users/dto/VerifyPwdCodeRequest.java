package com.career.careerlink.users.dto;

public record VerifyPwdCodeRequest(String userName, String email, String loginId, String code) {}