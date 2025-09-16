package com.career.careerlink.main.dto;

import java.util.List;
import java.util.Map;


public class MainJobsDtos {
    public record MainJobsItem(
            Long jobId,
            String title,
            String companyName,
            String companyLogoUrl,
            String jobField,
            String location,
            String employmentType,
            String experience,
            String education,
            String salary,
            String deadline,
            Long viewCount,
            Long appCount
    ) {
        public static MainJobsItem fromMap(Map<String, Object> m) {
            return new MainJobsItem(
                    numL(m.get("jobId")),
                    (String) m.get("title"),
                    (String) m.get("companyName"),
                    (String) m.get("companyLogoUrl"),
                    (String) m.get("jobField"),
                    (String) m.get("location"),
                    (String) m.get("employmentType"),
                    (String) m.get("experience"),
                    (String) m.get("education"),
                    (String) m.get("salary"),
                    m.get("deadline") != null ? String.valueOf(m.get("deadline")) : null,
                    numL(m.get("viewCount")),
                    numL(m.get("appCount"))
            );
        }

        private static Long numL(Object v) {
            return v == null ? null : ((Number) v).longValue();
        }

    }

    public record MainJobsResponse(
            List<MainJobsItem> items
    ) {
    }
}