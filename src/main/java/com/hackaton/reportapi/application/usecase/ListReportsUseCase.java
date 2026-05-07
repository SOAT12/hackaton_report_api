package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListReportsUseCase {

    private final ReportRepository reportRepository;

    public List<ReportResponseDTO> execute(ReportStatus status) {
        List<Report> reports = (status != null)
                ? reportRepository.findByStatus(status)
                : reportRepository.findAll();

        return reports.stream()
                .map(this::toResponseDTO)
                .toList();
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
