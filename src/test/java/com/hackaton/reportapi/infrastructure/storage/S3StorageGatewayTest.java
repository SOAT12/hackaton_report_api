package com.hackaton.reportapi.infrastructure.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class S3StorageGatewayTest {

    @Mock
    private S3Client s3Client;

    private S3StorageGateway s3StorageGateway;

    @BeforeEach
    void setUp() {
        s3StorageGateway = new S3StorageGateway(s3Client, "test-bucket");
    }

    @Test
    void upload_shouldReturnKey() {
        var key = "reports/report-id-1.json";

        var result = s3StorageGateway.upload(key, "{}");

        assertThat(result).isEqualTo(key);
    }

    @Test
    void upload_shouldCallS3PutObjectWithCorrectParameters() {
        var key = "reports/report-id-1.json";
        var content = "{\"id\":\"report-id-1\"}";

        s3StorageGateway.upload(key, content);

        var captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

        var request = captor.getValue();
        assertThat(request.bucket()).isEqualTo("test-bucket");
        assertThat(request.key()).isEqualTo(key);
        assertThat(request.contentType()).isEqualTo("application/json");
    }
}
