package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.event.ReportStatusEvent;
import com.hackaton.reportapi.domain.gateway.EventPublisherGateway;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.domain.gateway.StorageGateway;
import com.hackaton.reportapi.infrastructure.pdf.PdfGeneratorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class CreateReportUseCase {

    private final ReportRepository reportRepository;
    private final StorageGateway storageGateway;
    private final EventPublisherGateway eventPublisherGateway;
    private final PdfGeneratorService pdfGeneratorService;

    public CreateReportUseCase(
            ReportRepository reportRepository,
            StorageGateway storageGateway,
            EventPublisherGateway eventPublisherGateway,
            PdfGeneratorService pdfGeneratorService) {
        this.reportRepository = reportRepository;
        this.storageGateway = storageGateway;
        this.eventPublisherGateway = eventPublisherGateway;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public ReportResponseDTO execute(CreateReportRequestDTO request) {
        var id = UUID.randomUUID().toString();
        var pdfKey = "reports/" + id + ".pdf";
        var now = LocalDateTime.now();

        var report = Report.builder()
                .id(id)
                .diagramId(request.getDiagramId().toString())
                .title(request.getTitle())
                .report(request.getReport())
                .status(ReportStatus.COMPLETED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        var pdfBytes = pdfGeneratorService.generate(report);
        var reportUrl = storageGateway.uploadBytes(pdfKey, pdfBytes, "application/pdf");

        report.setReportUrl(reportUrl);
        var saved = reportRepository.save(report);

        eventPublisherGateway.publish(ReportStatusEvent.builder()
                .diagramId(saved.getDiagramId())
                .status(ReportStatus.COMPLETED)
                .reportLink(saved.getReportUrl())
                .notes("")
                .build());

        return toResponseDTO(saved);
    }

    private ReportResponseDTO toResponseDTO(Report report) {
        return ReportResponseDTO.builder()
                .id(report.getId())
                .diagramId(report.getDiagramId())
                .title(report.getTitle())
                .report(report.getReport())
                .status(report.getStatus())
                .reportUrl(report.getReportUrl())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
