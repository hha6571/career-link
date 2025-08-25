package com.career.careerlink.users.dto;

public record BasicVerifyCodeRequest(String userName, String email, String code) {}