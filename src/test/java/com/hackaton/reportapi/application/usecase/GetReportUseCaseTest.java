package com.hackaton.reportapi.application.usecase;

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
                .title("Financial Report")
                .description("Annual financial summary")
                .type(ReportType.FINANCIAL)
                .status(ReportStatus.COMPLETED)
                .createdBy("user-456")
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
        assertThat(result.getTitle()).isEqualTo("Financial Report");
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

        assertThat(result.getType()).isEqualTo(ReportType.FINANCIAL);
        assertThat(result.getCreatedBy()).isEqualTo("user-456");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
