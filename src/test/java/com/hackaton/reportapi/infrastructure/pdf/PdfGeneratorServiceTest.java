package com.hackaton.reportapi.infrastructure.pdf;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfGeneratorServiceTest {

    private PdfGeneratorService pdfGeneratorService;

    @BeforeEach
    void setUp() {
        pdfGeneratorService = new PdfGeneratorService();
    }

    @Test
    void generate_shouldReturnNonEmptyBytes() {
        var report = buildReport(
                List.of("API Gateway", "Lambda"),
                List.of("Single point of failure"),
                List.of("Add redundancy")
        );

        var result = pdfGeneratorService.generate(report);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    void generate_shouldProduceValidPdfBytes() {
        var report = buildReport(
                List.of("component_01"),
                List.of("risk_01"),
                List.of("rec_01")
        );

        var result = pdfGeneratorService.generate(report);

        // PDF files always start with the %PDF magic bytes
        assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void generate_withEmptyLists_shouldSucceed() {
        var report = buildReport(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        var result = pdfGeneratorService.generate(report);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    void generate_withNullLists_shouldSucceed() {
        var content = ReportContent.builder()
                .components(null)
                .risks(null)
                .recommendations(null)
                .build();

        var report = Report.builder()
                .id("report-id-1")
                .diagramId("diag-1")
                .title("Null Lists Report")
                .report(content)
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://example.com/report.pdf")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var result = pdfGeneratorService.generate(report);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    void generate_withMultipleItems_shouldSucceed() {
        var report = buildReport(
                List.of("API Gateway", "Lambda", "DynamoDB", "S3", "SQS"),
                List.of("High latency", "No retry logic", "Missing circuit breaker"),
                List.of("Add caching", "Implement retry", "Add health checks", "Use CDN")
        );

        var result = pdfGeneratorService.generate(report);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    void generate_withLongTextThatRequiresWrapping_shouldSucceed() {
        var longText = "This is a very long component description that should exceed the maximum line width " +
                "and therefore trigger the word wrapping logic inside the PDF generator service";

        var report = buildReport(
                List.of(longText),
                List.of(longText),
                List.of(longText)
        );

        var result = pdfGeneratorService.generate(report);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    void generate_withSingleWordItems_shouldSucceed() {
        var report = buildReport(
                List.of("Gateway"),
                List.of("Latency"),
                List.of("Caching")
        );

        var result = pdfGeneratorService.generate(report);

        assertThat(result).isNotNull().isNotEmpty();
    }

    private Report buildReport(List<String> components, List<String> risks, List<String> recommendations) {
        var content = ReportContent.builder()
                .components(components)
                .risks(risks)
                .recommendations(recommendations)
                .build();

        return Report.builder()
                .id("report-id-1")
                .diagramId("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55")
                .title("Architecture Report")
                .report(content)
                .status(ReportStatus.COMPLETED)
                .reportUrl("https://bucket.s3.us-east-1.amazonaws.com/reports/report-id-1.pdf")
                .createdAt(LocalDateTime.of(2026, 5, 21, 10, 30))
                .updatedAt(LocalDateTime.of(2026, 5, 21, 10, 30))
                .build();
    }
}
