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
                .title(report.getTitle())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdBy(report.getCreatedBy())
                .data(report.getData())
                .s3Key(report.getS3Key())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }

    public Report toDomain(ReportEntity entity) {
        if (entity == null) return null;
        return Report.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .data(entity.getData())
                .s3Key(entity.getS3Key())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
