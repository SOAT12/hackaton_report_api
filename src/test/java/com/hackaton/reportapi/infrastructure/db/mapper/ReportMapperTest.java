package com.hackaton.reportapi.infrastructure.db.mapper;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.entity.ReportType;
import com.hackaton.reportapi.infrastructure.db.entity.ReportEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportMapperTest {

    private ReportMapper reportMapper;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        reportMapper = new ReportMapper();
        now = LocalDateTime.now();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        var report = Report.builder()
                .id("id-1")
                .title("Test Report")
                .description("Description")
                .type(ReportType.SALES)
                .status(ReportStatus.PENDING)
                .createdBy("user-1")
                .data(Map.of("key", "value"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        var entity = reportMapper.toEntity(report);

        assertThat(entity.getId()).isEqualTo("id-1");
        assertThat(entity.getTitle()).isEqualTo("Test Report");
        assertThat(entity.getDescription()).isEqualTo("Description");
        assertThat(entity.getType()).isEqualTo(ReportType.SALES);
        assertThat(entity.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(entity.getCreatedBy()).isEqualTo("user-1");
        assertThat(entity.getData()).containsEntry("key", "value");
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
                .title("Domain Report")
                .description("Entity description")
                .type(ReportType.FINANCIAL)
                .status(ReportStatus.COMPLETED)
                .createdBy("user-2")
                .data(Map.of("amount", 99.9))
                .createdAt(now)
                .updatedAt(now)
                .build();

        var domain = reportMapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo("id-2");
        assertThat(domain.getTitle()).isEqualTo("Domain Report");
        assertThat(domain.getDescription()).isEqualTo("Entity description");
        assertThat(domain.getType()).isEqualTo(ReportType.FINANCIAL);
        assertThat(domain.getStatus()).isEqualTo(ReportStatus.COMPLETED);
        assertThat(domain.getCreatedBy()).isEqualTo("user-2");
        assertThat(domain.getData()).containsEntry("amount", 99.9);
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
                .title("Roundtrip")
                .type(ReportType.OPERATIONAL)
                .status(ReportStatus.PROCESSING)
                .createdBy("rt-user")
                .createdAt(now)
                .updatedAt(now)
                .build();

        var entity = reportMapper.toEntity(original);
        var restored = reportMapper.toDomain(entity);

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getTitle()).isEqualTo(original.getTitle());
        assertThat(restored.getType()).isEqualTo(original.getType());
        assertThat(restored.getStatus()).isEqualTo(original.getStatus());
    }
}
