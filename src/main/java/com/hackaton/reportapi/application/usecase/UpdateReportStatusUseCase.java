package com.hackaton.reportapi.application.usecase;

import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.application.dto.UpdateReportStatusRequestDTO;
import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateReportStatusUseCase {

    private final ReportRepository reportRepository;

    public ReportResponseDTO execute(String id, UpdateReportStatusRequestDTO request) {
        var report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));

        report.setStatus(request.getStatus());
        report.setUpdatedAt(LocalDateTime.now());

        var updated = reportRepository.save(report);

        return toResponseDTO(updated);
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
