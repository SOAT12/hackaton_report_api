package com.hackaton.reportapi.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.domain.gateway.StorageGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReportUseCase {

    private final ReportRepository reportRepository;
    private final StorageGateway storageGateway;
    private final ObjectMapper objectMapper;

    public ReportResponseDTO execute(CreateReportRequestDTO request) {
        var now = LocalDateTime.now();

        var report = Report.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .status(ReportStatus.PENDING)
                .createdBy(request.getCreatedBy())
                .data(request.getData())
                .createdAt(now)
                .updatedAt(now)
                .build();

        var saved = reportRepository.save(report);

        var s3Key = uploadToStorage(saved);
        saved.setS3Key(s3Key);
        var updated = reportRepository.save(saved);

        return toResponseDTO(updated);
    }

    private String uploadToStorage(Report report) {
        try {
            var key = "reports/" + report.getId() + ".json";
            var content = objectMapper.writeValueAsString(report);
            return storageGateway.upload(key, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize report for storage", e);
        }
    }

    private ReportResponseDTO toResponseDTO(Report report) {
        return ReportResponseDTO.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .type(report.getType())
                .status(report.getStatus())
                .createdBy(report.getCreatedBy())
                .data(report.getData())
                .s3Key(report.getS3Key())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
