package com.hackaton.reportapi.infrastructure.db.mapper;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.infrastructure.db.entity.ReportEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportEntity toEntity(Report report) {
        if (report == null) return null;
        return ReportEntity.builder()
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

    public Report toDomain(ReportEntity entity) {
        if (entity == null) return null;
        return Report.builder()
                .id(entity.getId())
                .diagramId(entity.getDiagramId())
                .title(entity.getTitle())
                .report(entity.getReport())
                .status(entity.getStatus())
                .reportUrl(entity.getReportUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
