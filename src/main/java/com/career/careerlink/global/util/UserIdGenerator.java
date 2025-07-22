package com.career.careerlink.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserIdGenerator {
    public static String generate(String prefix) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return prefix.toUpperCase() + LocalDateTime.now().format(formatter); // 예: AP20250710145521
    }
}