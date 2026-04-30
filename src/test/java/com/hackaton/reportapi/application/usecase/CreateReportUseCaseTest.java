package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReportUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private CreateReportUseCase createReportUseCase;

    private CreateReportRequestDTO request;
    private Report savedReport;

    @BeforeEach
    void setUp() {
        request = CreateReportRequestDTO.builder()
                .title("Q1 Sales Report")
                .description("Quarterly sales summary")
                .type(ReportType.SALES)
                .createdBy("user-123")
                .data(Map.of("total", 50000))
                .build();

        savedReport = Report.builder()
                .id("report-id-1")
                .title("Q1 Sales Report")
                .description("Quarterly sales summary")
                .type(ReportType.SALES)
                .status(ReportStatus.PENDING)
                .createdBy("user-123")
                .data(Map.of("total", 50000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_shouldCreateReportWithPendingStatus() {
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        var result = createReportUseCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("report-id-1");
        assertThat(result.getTitle()).isEqualTo("Q1 Sales Report");
        assertThat(result.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(result.getType()).isEqualTo(ReportType.SALES);
        assertThat(result.getCreatedBy()).isEqualTo("user-123");
    }

    @Test
    void execute_shouldSaveReportWithCorrectFields() {
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        createReportUseCase.execute(request);

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void execute_shouldReturnResponseWithAllFields() {
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        var result = createReportUseCase.execute(request);

        assertThat(result.getDescription()).isEqualTo("Quarterly sales summary");
        assertThat(result.getData()).containsEntry("total", 50000);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
