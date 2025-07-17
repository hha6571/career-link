package com.career.careerlink.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserIdGenerator {
    public static String generate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "USR" + LocalDateTime.now().format(formatter); // 예: AP20250710145521
    }
}