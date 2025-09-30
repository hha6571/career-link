package com.career.careerlink.job.dto;

import com.career.careerlink.common.enums.YnType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class JobCardResponse {
    Integer jobId;           // jobPostingId
    String title;
    String employerId;
    String companyName;
    String companyLogoUrl;
    String jobField;
    String location;
    String employmentType;
    String careerLevel;
    String educationLevel;
    String salary;
    LocalDateTime postedAt;  // createdAt
    LocalDate deadline;      // applicationDeadline
    YnType isActive;
    YnType isDeleted;

    Boolean scrapped;
}