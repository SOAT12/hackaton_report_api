package com.hackaton.reportapi.infrastructure.db.mapper;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.infrastructure.db.entity.ReportEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportMapperTest {

    private ReportMapper reportMapper;
    private LocalDateTime now;
    private ReportContent content;

    @BeforeEach
    void setUp() {
        reportMapper = new ReportMapper();
        now = LocalDateTime.now();
        content = ReportContent.builder()
                .components(List.of("component_01"))
                .risks(List.of("risk_01"))
                .recommendations(List.of("rec_01"))
                .build();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        var report = Report.builder()
                .id("id-1")
                .diagramId("diagram-id-1")
                .title("Test Report")
                .report(content)
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://localhost:8080/api/reports/id-1")
                .createdAt(now)
                .updatedAt(now)
                .build();

        var entity = reportMapper.toEntity(report);

        assertThat(entity.getId()).isEqualTo("id-1");
        assertThat(entity.getDiagramId()).isEqualTo("diagram-id-1");
        assertThat(entity.getTitle()).isEqualTo("Test Report");
        assertThat(entity.getReport()).isEqualTo(content);
        assertThat(entity.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(entity.getReportUrl()).isEqualTo("http://localhost:8080/api/reports/id-1");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toEntity_withNullInput_shouldReturnNull() {
        assertThat(reportMapper.toEntity(null)).isNull();
    }

    @Test
    void toDomain_shouldMapAllFields() {
        var entity = ReportEntity.builder()
                .id("id-2")
                .diagramId("diagram-id-2")
                .title("Domain Report")
                .report(content)
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://localhost:8080/api/reports/id-2")
                .createdAt(now)
                .updatedAt(now)
                .build();

        var domain = reportMapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo("id-2");
        assertThat(domain.getDiagramId()).isEqualTo("diagram-id-2");
        assertThat(domain.getTitle()).isEqualTo("Domain Report");
        assertThat(domain.getReport()).isEqualTo(content);
        assertThat(domain.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(domain.getReportUrl()).isEqualTo("http://localhost:8080/api/reports/id-2");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_withNullInput_shouldReturnNull() {
        assertThat(reportMapper.toDomain(null)).isNull();
    }

    @Test
    void roundTrip_shouldPreserveAllData() {
        var original = Report.builder()
                .id("rt-id")
                .diagramId("rt-diagram-id")
                .title("Roundtrip")
                .report(content)
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://localhost:8080/api/reports/rt-id")
                .createdAt(now)
                .updatedAt(now)
                .build();

        var entity = reportMapper.toEntity(original);
        var restored = reportMapper.toDomain(entity);

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getDiagramId()).isEqualTo(original.getDiagramId());
        assertThat(restored.getTitle()).isEqualTo(original.getTitle());
        assertThat(restored.getStatus()).isEqualTo(original.getStatus());
        assertThat(restored.getReportUrl()).isEqualTo(original.getReportUrl());
    }
}
