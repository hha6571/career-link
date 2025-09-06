package com.career.careerlink.job.spec;

import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import org.springframework.data.jpa.domain.Specification;

public class JobPostingSpecs {

    public static Specification<JobPosting> isActive() {
        return (root, q, cb) -> cb.equal(root.get("isActive"), AgreementStatus.Y);
    }

    public static Specification<JobPosting> keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String like = "%" + keyword.trim() + "%";
        return (root, q, cb) ->
                cb.or(
                        cb.like(root.get("title"), like),
                        cb.like(root.get("description"), like)
                );
    }

    public static Specification<JobPosting> jobFieldEq(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("jobFieldCode"), code);
    }

    public static Specification<JobPosting> locationEq(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("locationCode"), code);
    }

    public static Specification<JobPosting> empTypeEq(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("employmentTypeCode"), code);
    }

    public static Specification<JobPosting> educationEq(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("educationLevelCode"), code);
    }

    public static Specification<JobPosting> careerLevelEq(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("careerLevelCode"), code);
    }

    public static Specification<JobPosting> salaryEq(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("salaryCode"), code);
    }


}
