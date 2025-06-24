package com.career.careerlink.main.dto;

import java.util.List;
import java.util.Map;


public class MainEmployersDtos {
    public record MainEmployersItem(
            String employerId,
            String companyName,
            String companyLogoUrl,
            Long postingCount
    ) {
        public static MainEmployersItem fromMap(Map<String, Object> m) {
            return new MainEmployersItem(
                    (String) m.get("employerId"),
                    (String) m.get("companyName"),
                    (String) m.get("companyLogoUrl"),
                    (Long) m.get("postingCount")
            );
        }
    }

    public record MainEmployersResponse(
            List<MainEmployersItem> items
    ) {
    }
}