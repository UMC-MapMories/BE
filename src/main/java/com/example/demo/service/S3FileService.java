package com.example.demo.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService {
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public Map<String, String> getPresignedUrl(String prefix, String fileName, String contentType) {
        if (!prefix.isEmpty()) {
            fileName = createPath(prefix, fileName);
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, fileName, contentType);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return Map.of("url", url.toString());
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName, String contentType) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

        // ACL 설정 제거 가능 (필요 없을 경우)
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        // Content-Type 강제 지정
        generatePresignedUrlRequest.addRequestParameter("response-content-type", contentType);

        // 브라우저에서 바로 보이게 하기 위한 설정
        generatePresignedUrlRequest.addRequestParameter("response-content-disposition", "inline");

        return generatePresignedUrlRequest;
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + "-" + fileName);
    }
}