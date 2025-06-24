package com.career.careerlink.global.s3;

public enum S3UploadType {
    PROFILE_IMAGE("profile-images/"),
    BUSINESS_CERTIFICATE("bizRegistrations/"),
    COMPANY_LOGO("company-logos/"),
    JOB_POSTING("job-postings/"),
    NOTICE_FILE("notice/");

    private final String dir;

    S3UploadType(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }
}