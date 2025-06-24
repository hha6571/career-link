package com.career.careerlink.users.dto;

public record ResetPasswordRequest(String resetToken, String newPassword) {}
