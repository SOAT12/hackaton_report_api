package com.hackaton.reportapi.infrastructure.storage;

import com.hackaton.reportapi.domain.gateway.StorageGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;

@Service
public class S3StorageGateway implements StorageGateway {

    private final S3Client s3Client;
    private final String bucketName;
    private final String baseUrl;

    public S3StorageGateway(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.base-url}") String baseUrl) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.baseUrl = baseUrl;
    }

    @Override
    public String upload(String key, String content) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("application/json")
                        .build(),
                RequestBody.fromString(content, StandardCharsets.UTF_8)
        );
        return key;
    }

    @Override
    public String uploadBytes(String key, byte[] content, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(content)
        );
        return baseUrl + "/" + key;
    }
}
