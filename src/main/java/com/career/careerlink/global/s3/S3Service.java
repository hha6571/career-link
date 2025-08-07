package com.career.careerlink.global.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(S3UploadType uploadType, MultipartFile file) {
        String key = uploadType.getDir() + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(bucket, key, inputStream);
            return getFileUrl(key);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public Resource downloadFile(String key) {
        return s3Template.download(bucket, key);
    }

    public void deleteFile(String key) {
        s3Template.deleteObject(bucket, key);
    }

    public String getFileUrl(String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }
}