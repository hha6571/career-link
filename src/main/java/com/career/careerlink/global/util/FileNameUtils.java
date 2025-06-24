package com.career.careerlink.global.util;

public class FileNameUtils {
    public static String extractOriginalFileName(String storedFileName) {
        if (storedFileName == null) {
            return null;
        }

        int underscoreIndex = storedFileName.indexOf('_');
        if (underscoreIndex == -1 || underscoreIndex == storedFileName.length() - 1) {
            return storedFileName;
        }

        return storedFileName.substring(underscoreIndex + 1);
    }
}
