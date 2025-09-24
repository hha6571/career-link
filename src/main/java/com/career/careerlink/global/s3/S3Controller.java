package com.career.careerlink.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.career.careerlink.global.util.FileNameUtils.extractOriginalFileName;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file,
                                         @RequestParam S3UploadType uploadType) {
        String url = s3Service.uploadFile(uploadType, file);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String key) {
        Resource resource = s3Service.downloadFile(key);
        String originalName = extractOriginalFileName(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.ok("삭제 완료: " + key);
    }
}
