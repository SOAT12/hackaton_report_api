package com.hackaton.reportapi.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.event.ReportStatusEvent;
import com.hackaton.reportapi.domain.gateway.EventPublisherGateway;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.domain.gateway.StorageGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateReportUseCase {

    private final ReportRepository reportRepository;
    private final StorageGateway storageGateway;
    private final EventPublisherGateway eventPublisherGateway;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public CreateReportUseCase(
            ReportRepository reportRepository,
            StorageGateway storageGateway,
            EventPublisherGateway eventPublisherGateway,
            ObjectMapper objectMapper,
            @Value("${app.base-url}") String baseUrl) {
        this.reportRepository = reportRepository;
        this.storageGateway = storageGateway;
        this.eventPublisherGateway = eventPublisherGateway;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
    }

    public ReportResponseDTO execute(CreateReportRequestDTO request) {
        var id = UUID.randomUUID().toString();
        var reportUrl = baseUrl + "/api/reports/" + id;
        var now = LocalDateTime.now();

        var report = Report.builder()
                .id(id)
                .diagramId(request.getDiagramId().toString())
                .title(request.getTitle())
                .report(request.getReport())
                .status(ReportStatus.COMPLETED)
                .reportUrl(reportUrl)
                .createdAt(now)
                .updatedAt(now)
                .build();

        var saved = reportRepository.save(report);

        uploadToStorage(saved);

        eventPublisherGateway.publish(ReportStatusEvent.builder()
                .diagramId(saved.getDiagramId())
                .status(ReportStatus.COMPLETED)
                .reportLink(saved.getReportUrl())
                .notes("")
                .build());

        return toResponseDTO(saved);
    }

    private void uploadToStorage(Report report) {
        try {
            var key = "reports/" + report.getId() + ".json";
            var payload = Map.of(
                    "reportId", report.getId(),
                    "diagramId", report.getDiagramId(),
                    "url", report.getReportUrl()
            );
            var content = objectMapper.writeValueAsString(payload);
            storageGateway.upload(key, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize report reference for storage", e);
        }
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
