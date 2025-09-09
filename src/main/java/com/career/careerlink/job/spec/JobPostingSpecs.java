package com.career.careerlink.job.spec;

import com.career.careerlink.job.entity.JobPosting;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class JobPostingSpecs {

    public static Specification<JobPosting> isActive() {
        return (root, q, cb) -> cb.equal(root.get("isActive"), AgreementStatus.Y);
    }

    public static Specification<JobPosting> keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String like = "%" + keyword.trim() + "%";
        return (root, q, cb) -> {
            var employer = root.join("employer", JoinType.LEFT);
            return cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("description"), like),
                    cb.like(employer.get("companyName"), like)  // 회사 이름 추가
            );
        };
    }

    public static <T> Specification<T> inList(String fieldName, List<String> values) {
        if (values == null || values.isEmpty()) return null;
        if (values.size() == 1) {
            String v = values.get(0);
            if (v == null || v.isBlank()) return null;
            return (root, q, cb) -> cb.equal(root.get(fieldName), v);
        }
        var nonEmpty = values.stream().filter(v -> v != null && !v.isBlank()).toList();
        if (nonEmpty.isEmpty()) return null;
        return (root, q, cb) -> root.get(fieldName).in(nonEmpty);
    }

    public static Specification<JobPosting> jobFieldEq(String code) {
        return inList("jobFieldCode", List.of(code));
    }
    public static Specification<JobPosting> locationEq(String code) {
        return inList("locationCode", List.of(code));
    }
    public static Specification<JobPosting> empTypeEq(String code) {
        return inList("employmentTypeCode", List.of(code));
    }
    public static Specification<JobPosting> educationEq(String code) {
        return inList("educationLevelCode", List.of(code));
    }
    public static Specification<JobPosting> careerLevelEq(String code) {
        return inList("careerLevelCode", List.of(code));
    }
    public static Specification<JobPosting> salaryEq(String code) {
        return inList("salaryCode", List.of(code));
    }

    public static Specification<JobPosting> jobFieldIn(List<String> codes) {
        return inList("jobFieldCode", codes);
    }
    public static Specification<JobPosting> locationIn(List<String> codes) {
        return inList("locationCode", codes);
    }
    public static Specification<JobPosting> empTypeIn(List<String> codes) {
        return inList("employmentTypeCode", codes);
    }
    public static Specification<JobPosting> educationIn(List<String> codes) {
        return inList("educationLevelCode", codes);
    }
    public static Specification<JobPosting> careerLevelIn(List<String> codes) {
        return inList("careerLevelCode", codes);
    }
    public static Specification<JobPosting> salaryIn(List<String> codes) {
        return inList("salaryCode", codes);
    }

}
