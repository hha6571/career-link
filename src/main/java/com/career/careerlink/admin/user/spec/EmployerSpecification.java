package com.career.careerlink.admin.user.spec;

import com.career.careerlink.employers.info.entiry.Employer;
import com.career.careerlink.users.entity.enums.AgreementStatus;
import org.springframework.data.jpa.domain.Specification;

public class EmployerSpecification {

    public static Specification<Employer> hasCompanyNameLike(String companyName) {
        return (root, query, cb) -> {
            if (companyName == null || companyName.isBlank()) return null;
            return cb.like(root.get("companyName"), "%" + companyName + "%");
        };
    }

    public static Specification<Employer> hasApprovalStatus(AgreementStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("isApproved"), status);
        };
    }
}
