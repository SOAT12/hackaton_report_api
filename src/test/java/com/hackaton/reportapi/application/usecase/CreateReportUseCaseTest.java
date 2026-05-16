package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.event.ReportStatusEvent;
import com.hackaton.reportapi.domain.gateway.EventPublisherGateway;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.domain.gateway.StorageGateway;
import com.hackaton.reportapi.infrastructure.pdf.PdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReportUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private StorageGateway storageGateway;

    @Mock
    private EventPublisherGateway eventPublisherGateway;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    private CreateReportUseCase createReportUseCase;

    private static final String DIAGRAM_ID = "3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55";
    private static final String REPORT_URL = "http://localhost:4566/reports-bucket/reports/report-id-1.pdf";

    private CreateReportRequestDTO request;
    private Report savedReport;

    @BeforeEach
    void setUp() {
        createReportUseCase = new CreateReportUseCase(
                reportRepository, storageGateway, eventPublisherGateway, pdfGeneratorService);

        var content = ReportContent.builder()
                .components(List.of("component_01", "component_02"))
                .risks(List.of("risk_01"))
                .recommendations(List.of("rec_01"))
                .build();

        request = CreateReportRequestDTO.builder()
                .diagramId(UUID.fromString(DIAGRAM_ID))
                .title("Architecture Report")
                .report(content)
                .build();

        savedReport = Report.builder()
                .id("report-id-1")
                .diagramId(DIAGRAM_ID)
                .title("Architecture Report")
                .report(content)
                .status(ReportStatus.COMPLETED)
                .reportUrl(REPORT_URL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(pdfGeneratorService.generate(any(Report.class))).thenReturn(new byte[]{});
        when(storageGateway.uploadBytes(anyString(), any(byte[].class), anyString())).thenReturn(REPORT_URL);
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);
    }

    @Test
    void execute_shouldCreateReportWithCompletedStatus() {
        var result = createReportUseCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(result.getTitle()).isEqualTo("Architecture Report");
        assertThat(result.getDiagramId()).isEqualTo(DIAGRAM_ID);
    }

    @Test
    void execute_shouldGenerateAndUploadPdfToS3() {
        createReportUseCase.execute(request);

        verify(pdfGeneratorService).generate(any(Report.class));
        verify(storageGateway).uploadBytes(anyString(), any(byte[].class), anyString());
    }

    @Test
    void execute_shouldPublishEventToSqsAfterCreation() {
        createReportUseCase.execute(request);

        var captor = ArgumentCaptor.forClass(ReportStatusEvent.class);
        verify(eventPublisherGateway).publish(captor.capture());

        var event = captor.getValue();
        assertThat(event.getDiagramId()).isEqualTo(DIAGRAM_ID);
        assertThat(event.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(event.getNotes()).isEmpty();
    }

    @Test
    void execute_shouldReturnResponseWithAllFields() {
        var result = createReportUseCase.execute(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getReport()).isNotNull();
        assertThat(result.getReportUrl()).contains(".pdf");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void execute_shouldUploadPdfWithCorrectKey() {
        createReportUseCase.execute(request);

        var keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(storageGateway).uploadBytes(keyCaptor.capture(), any(byte[].class), anyString());
        assertThat(keyCaptor.getValue()).startsWith("reports/").endsWith(".pdf");
    }
}
