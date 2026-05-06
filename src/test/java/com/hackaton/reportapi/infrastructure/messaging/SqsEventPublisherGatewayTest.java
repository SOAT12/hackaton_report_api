package com.hackaton.reportapi.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.event.ReportStatusEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqsEventPublisherGatewayTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    private SqsEventPublisherGateway gateway;

    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789/test-queue";

    @BeforeEach
    void setUp() {
        gateway = new SqsEventPublisherGateway(sqsClient, objectMapper, QUEUE_URL);
    }

    @Test
    void publish_shouldSendMessageToSqsWithCorrectQueueUrl() throws JsonProcessingException {
        var event = buildEvent(ReportStatus.COMPLETED);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"reportId\":\"report-id-1\"}");

        gateway.publish(event);

        var captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient).sendMessage(captor.capture());
        assertThat(captor.getValue().queueUrl()).isEqualTo(QUEUE_URL);
    }

    @Test
    void publish_shouldSendSerializedEventAsMessageBody() throws JsonProcessingException {
        var event = buildEvent(ReportStatus.COMPLETED);
        var expectedBody = "{\"reportId\":\"report-id-1\",\"status\":\"COMPLETED\"}";
        when(objectMapper.writeValueAsString(event)).thenReturn(expectedBody);

        gateway.publish(event);

        var captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient).sendMessage(captor.capture());
        assertThat(captor.getValue().messageBody()).isEqualTo(expectedBody);
    }

    @Test
    void publish_shouldThrowWhenSerializationFails() throws JsonProcessingException {
        var event = buildEvent(ReportStatus.FAILED);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("error") {});

        assertThatThrownBy(() -> gateway.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize report status event");
    }

    private ReportStatusEvent buildEvent(ReportStatus status) {
        return ReportStatusEvent.builder()
                .reportId("report-id-1")
                .status(status)
                .s3Key("reports/report-id-1.json")
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
