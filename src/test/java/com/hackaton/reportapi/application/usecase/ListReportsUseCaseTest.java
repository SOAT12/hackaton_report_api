package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.entity.ReportType;
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
        reports = List.of(
                Report.builder()
                        .id("id-1")
                        .title("Report 1")
                        .type(ReportType.SALES)
                        .status(ReportStatus.PENDING)
                        .createdBy("user-1")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Report.builder()
                        .id("id-2")
                        .title("Report 2")
                        .type(ReportType.FINANCIAL)
                        .status(ReportStatus.COMPLETED)
                        .createdBy("user-2")
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
        var pending = List.of(reports.get(0));
        when(reportRepository.findByStatus(ReportStatus.PENDING)).thenReturn(pending);

        var result = listReportsUseCase.execute(ReportStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReportStatus.PENDING);
        verify(reportRepository).findByStatus(ReportStatus.PENDING);
    }

    @Test
    void execute_shouldMapAllReportFields() {
        when(reportRepository.findAll()).thenReturn(reports);

        var result = listReportsUseCase.execute(null);

        assertThat(result.get(0).getId()).isEqualTo("id-1");
        assertThat(result.get(0).getTitle()).isEqualTo("Report 1");
        assertThat(result.get(1).getId()).isEqualTo("id-2");
    }

    @Test
    void execute_withNoReports_shouldReturnEmptyList() {
        when(reportRepository.findAll()).thenReturn(List.of());

        var result = listReportsUseCase.execute(null);

        assertThat(result).isEmpty();
    }
}
