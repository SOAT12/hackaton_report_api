package com.hackaton.reportapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import java.util.Optional;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public S3Client s3Client(
            @Value("${aws.region:us-east-1}") String region,
            @Value("${aws.endpoint-override:}") String endpointOverride) {
        var builder = S3Client.builder().region(Region.of(region));
        Optional.of(endpointOverride).filter(e -> !e.isBlank()).ifPresent(endpoint -> {
            builder.endpointOverride(URI.create(endpoint));
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")));
            builder.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        });
        return builder.build();
    }

    @Bean
    public SqsClient sqsClient(
            @Value("${aws.region:us-east-1}") String region,
            @Value("${aws.endpoint-override:}") String endpointOverride) {
        var builder = SqsClient.builder().region(Region.of(region));
        Optional.of(endpointOverride).filter(e -> !e.isBlank()).ifPresent(endpoint -> {
            builder.endpointOverride(URI.create(endpoint));
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")));
        });
        return builder.build();
    }
}
