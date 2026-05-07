package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListReportsUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ListReportsUseCase listReportsUseCase;

    private List<Report> reports;

    @BeforeEach
    void setUp() {
        var content = ReportContent.builder()
                .components(List.of("component_01"))
                .risks(List.of("risk_01"))
                .recommendations(List.of("rec_01"))
                .build();

        reports = List.of(
                Report.builder()
                        .id("id-1")
                        .diagramId("diagram-id-1")
                        .title("Report 1")
                        .report(content)
                        .status(ReportStatus.COMPLETED)
                        .reportUrl("http://localhost:8080/api/reports/id-1")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Report.builder()
                        .id("id-2")
                        .diagramId("diagram-id-2")
                        .title("Report 2")
                        .report(content)
                        .status(ReportStatus.COMPLETED)
                        .reportUrl("http://localhost:8080/api/reports/id-2")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Test
    void execute_withNullStatus_shouldReturnAllReports() {
        when(reportRepository.findAll()).thenReturn(reports);

        var result = listReportsUseCase.execute(null);

        assertThat(result).hasSize(2);
        verify(reportRepository).findAll();
    }

    @Test
    void execute_withStatus_shouldFilterByStatus() {
        var completed = List.of(reports.get(0));
        when(reportRepository.findByStatus(ReportStatus.COMPLETED)).thenReturn(completed);

        var result = listReportsUseCase.execute(ReportStatus.COMPLETED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReportStatus.COMPLETED);
        verify(reportRepository).findByStatus(ReportStatus.COMPLETED);
    }

    @Test
    void execute_shouldMapAllReportFields() {
        when(reportRepository.findAll()).thenReturn(reports);

        var result = listReportsUseCase.execute(null);

        assertThat(result.get(0).getId()).isEqualTo("id-1");
        assertThat(result.get(0).getDiagramId()).isEqualTo("diagram-id-1");
        assertThat(result.get(1).getId()).isEqualTo("id-2");
    }

    @Test
    void execute_withNoReports_shouldReturnEmptyList() {
        when(reportRepository.findAll()).thenReturn(List.of());

        var result = listReportsUseCase.execute(null);

        assertThat(result).isEmpty();
    }
}
