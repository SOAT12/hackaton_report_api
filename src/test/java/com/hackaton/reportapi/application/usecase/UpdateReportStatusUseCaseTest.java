package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.UpdateReportStatusRequestDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.entity.ReportType;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private UpdateReportStatusUseCase updateReportStatusUseCase;

    private Report existingReport;

    @BeforeEach
    void setUp() {
        existingReport = Report.builder()
                .id("report-id-1")
                .title("Inventory Report")
                .type(ReportType.INVENTORY)
                .status(ReportStatus.PENDING)
                .createdBy("user-789")
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Test
    void execute_shouldUpdateStatusSuccessfully() {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.COMPLETED);
        var updatedReport = Report.builder()
                .id("report-id-1")
                .title("Inventory Report")
                .type(ReportType.INVENTORY)
                .status(ReportStatus.COMPLETED)
                .createdBy("user-789")
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reportRepository.findById("report-id-1")).thenReturn(Optional.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(updatedReport);

        var result = updateReportStatusUseCase.execute("report-id-1", request);

        assertThat(result.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void execute_shouldThrowWhenReportNotFound() {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.FAILED);
        when(reportRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateReportStatusUseCase.execute("missing-id", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing-id");
    }

    @Test
    void execute_shouldUpdateToProcessingStatus() {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.PROCESSING);
        var updatedReport = Report.builder()
                .id("report-id-1")
                .status(ReportStatus.PROCESSING)
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(reportRepository.findById("report-id-1")).thenReturn(Optional.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(updatedReport);

        var result = updateReportStatusUseCase.execute("report-id-1", request);

        assertThat(result.getStatus()).isEqualTo(ReportStatus.PROCESSING);
    }
}
