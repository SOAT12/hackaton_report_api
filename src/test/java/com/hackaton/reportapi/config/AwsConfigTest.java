package com.hackaton.reportapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

import static org.assertj.core.api.Assertions.assertThat;

class AwsConfigTest {

    @Test
    void awsCredentialsProvider_shouldReturnStaticProviderWithSessionCredentials() {
        var config = new AwsConfig();
        ReflectionTestUtils.setField(config, "accessKey", "test-access-key");
        ReflectionTestUtils.setField(config, "secretKey", "test-secret-key");
        ReflectionTestUtils.setField(config, "sessionToken", "test-session-token");

        var provider = config.awsCredentialsProvider();

        assertThat(provider).isNotNull();
        var credentials = provider.resolveCredentials();
        assertThat(credentials).isInstanceOf(AwsSessionCredentials.class);

        var sessionCredentials = (AwsSessionCredentials) credentials;
        assertThat(sessionCredentials.accessKeyId()).isEqualTo("test-access-key");
        assertThat(sessionCredentials.secretAccessKey()).isEqualTo("test-secret-key");
        assertThat(sessionCredentials.sessionToken()).isEqualTo("test-session-token");
    }
}
