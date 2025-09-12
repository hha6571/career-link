package com.career.careerlink.job.dto;

import java.util.List;
import java.util.Map;


public class HotDtos {
    public record HotRequest(
            Integer limit,
            String cursor
    ) {}


    public record HotItem(
            Integer jobId,
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
            Integer viewCount
    ) {
        @SuppressWarnings("unchecked")
        public static HotItem fromMap(Map<String, Object> m) {
            return new HotItem(
                    (Integer) m.get("jobId"),
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
                    (Integer) m.get("viewCount")
            );
        }
    }


    public record HotResponse(
            List<HotItem> items,
            String nextCursor,
            boolean hasMore
    ) {}
}