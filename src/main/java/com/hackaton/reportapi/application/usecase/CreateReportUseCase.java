package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateReportUseCase {

    private final ReportRepository reportRepository;

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

        return toResponseDTO(saved);
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
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
