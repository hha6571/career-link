package com.career.careerlink.job.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class JobCardResponse {
    Integer jobId;           // jobPostingId
    String title;
    String employerId;
    String jobField;
    String location;
    String employmentType;
    String careerLevel;
    String educationLevel;
    String salary;
    LocalDateTime postedAt;  // createdAt
    LocalDate deadline;      // applicationDeadline
}