package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.UpdateReportStatusRequestDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.event.ReportStatusEvent;
import com.hackaton.reportapi.domain.gateway.EventPublisherGateway;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateReportStatusUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private EventPublisherGateway eventPublisherGateway;

    @InjectMocks
    private UpdateReportStatusUseCase updateReportStatusUseCase;

    private Report existingReport;

    @BeforeEach
    void setUp() {
        existingReport = Report.builder()
                .id("report-id-1")
                .diagramId("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55")
                .title("Architecture Report")
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://localhost:8080/api/reports/report-id-1")
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Test
    void execute_shouldUpdateStatusSuccessfully() {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.FAILED);
        var updatedReport = Report.builder()
                .id("report-id-1")
                .diagramId("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55")
                .status(ReportStatus.FAILED)
                .reportUrl(existingReport.getReportUrl())
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reportRepository.findById("report-id-1")).thenReturn(Optional.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(updatedReport);

        var result = updateReportStatusUseCase.execute("report-id-1", request);

        assertThat(result.getStatus()).isEqualTo(ReportStatus.FAILED);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void execute_shouldPublishEventToSqsAfterStatusUpdate() {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.FAILED);
        var updatedReport = Report.builder()
                .id("report-id-1")
                .diagramId("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55")
                .status(ReportStatus.FAILED)
                .reportUrl("http://localhost:8080/api/reports/report-id-1")
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reportRepository.findById("report-id-1")).thenReturn(Optional.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(updatedReport);

        updateReportStatusUseCase.execute("report-id-1", request);

        var captor = ArgumentCaptor.forClass(ReportStatusEvent.class);
        verify(eventPublisherGateway).publish(captor.capture());

        var event = captor.getValue();
        assertThat(event.getDiagramId()).isEqualTo("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55");
        assertThat(event.getStatus()).isEqualTo(ReportStatus.FAILED);
        assertThat(event.getReportLink()).isEqualTo("http://localhost:8080/api/reports/report-id-1");
        assertThat(event.getNotes()).isEmpty();
    }

    @Test
    void execute_shouldThrowWhenReportNotFound() {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.FAILED);
        when(reportRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateReportStatusUseCase.execute("missing-id", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing-id");
    }
}
