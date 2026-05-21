package com.hackaton.reportapi.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigTest {

    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = new AppConfig();
    }

    @Test
    void objectMapper_shouldSerializeDatesAsIso() throws Exception {
        var mapper = appConfig.objectMapper();
        var now = java.time.LocalDateTime.of(2026, 5, 21, 10, 30, 0);

        var json = mapper.writeValueAsString(now);

        assertThat(json).contains("2026-05-21");
    }

    @Test
    void s3Client_withEndpointOverride_shouldBuildSuccessfully() {
        var client = appConfig.s3Client("us-east-1", "http://localhost:4566");

        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    void s3Client_withoutEndpointOverride_shouldBuildSuccessfully() {
        var client = appConfig.s3Client("us-east-1", "");

        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    void sqsClient_withEndpointOverride_shouldBuildSuccessfully() {
        var client = appConfig.sqsClient("us-east-1", "http://localhost:4566");

        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    void sqsClient_withoutEndpointOverride_shouldBuildSuccessfully() {
        var client = appConfig.sqsClient("us-east-1", "");

        assertThat(client).isNotNull();
        client.close();
    }
}
