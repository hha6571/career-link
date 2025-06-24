package com.career.careerlink.users.dto;

public record PwdVerificationRequest(String userName, String email, String loginId) {}