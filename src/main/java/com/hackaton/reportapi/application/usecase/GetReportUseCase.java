package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetReportUseCase {

    private final ReportRepository reportRepository;

    public ReportResponseDTO execute(String id) {
        var report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));

        return toResponseDTO(report);
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
