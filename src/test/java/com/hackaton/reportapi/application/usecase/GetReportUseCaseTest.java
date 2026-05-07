package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReportUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private GetReportUseCase getReportUseCase;

    private Report report;

    @BeforeEach
    void setUp() {
        report = Report.builder()
                .id("report-id-1")
                .diagramId("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55")
                .title("Architecture Report")
                .report(ReportContent.builder()
                        .components(List.of("component_01"))
                        .risks(List.of("risk_01"))
                        .recommendations(List.of("rec_01"))
                        .build())
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://localhost:8080/api/reports/report-id-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_shouldReturnReportWhenFound() {
        when(reportRepository.findById("report-id-1")).thenReturn(Optional.of(report));

        var result = getReportUseCase.execute("report-id-1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("report-id-1");
        assertThat(result.getTitle()).isEqualTo("Architecture Report");
        assertThat(result.getStatus()).isEqualTo(ReportStatus.COMPLETED);
    }

    @Test
    void execute_shouldThrowWhenNotFound() {
        when(reportRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getReportUseCase.execute("non-existent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("non-existent");
    }

    @Test
    void execute_shouldMapAllFields() {
        when(reportRepository.findById("report-id-1")).thenReturn(Optional.of(report));

        var result = getReportUseCase.execute("report-id-1");

        assertThat(result.getDiagramId()).isEqualTo("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55");
        assertThat(result.getReport()).isNotNull();
        assertThat(result.getReportUrl()).isEqualTo("http://localhost:8080/api/reports/report-id-1");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
