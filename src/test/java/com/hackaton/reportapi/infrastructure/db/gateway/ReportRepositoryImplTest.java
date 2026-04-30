package com.hackaton.reportapi.infrastructure.db.gateway;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.entity.ReportType;
import com.hackaton.reportapi.infrastructure.db.entity.ReportEntity;
import com.hackaton.reportapi.infrastructure.db.mapper.ReportMapper;
import com.hackaton.reportapi.infrastructure.db.repository.SpringDataReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportRepositoryImplTest {

    @Mock
    private SpringDataReportRepository springDataReportRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportRepositoryImpl reportRepositoryImpl;

    private Report report;
    private ReportEntity entity;

    @BeforeEach
    void setUp() {
        report = Report.builder()
                .id("id-1")
                .title("Test")
                .type(ReportType.SALES)
                .status(ReportStatus.PENDING)
                .createdBy("user-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entity = ReportEntity.builder()
                .id("id-1")
                .title("Test")
                .type(ReportType.SALES)
                .status(ReportStatus.PENDING)
                .createdBy("user-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_shouldPersistAndReturn() {
        when(reportMapper.toEntity(report)).thenReturn(entity);
        when(springDataReportRepository.save(entity)).thenReturn(entity);
        when(reportMapper.toDomain(entity)).thenReturn(report);

        var result = reportRepositoryImpl.save(report);

        assertThat(result).isEqualTo(report);
        verify(springDataReportRepository).save(entity);
    }

    @Test
    void findById_shouldReturnMappedDomain() {
        when(springDataReportRepository.findById("id-1")).thenReturn(Optional.of(entity));
        when(reportMapper.toDomain(entity)).thenReturn(report);

        var result = reportRepositoryImpl.findById("id-1");

        assertThat(result).isPresent().contains(report);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(springDataReportRepository.findById("missing")).thenReturn(Optional.empty());

        var result = reportRepositoryImpl.findById("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnMappedList() {
        when(springDataReportRepository.findAll()).thenReturn(List.of(entity));
        when(reportMapper.toDomain(entity)).thenReturn(report);

        var result = reportRepositoryImpl.findAll();

        assertThat(result).hasSize(1).contains(report);
    }

    @Test
    void findByStatus_shouldFilterAndMap() {
        when(springDataReportRepository.findByStatus(ReportStatus.PENDING)).thenReturn(List.of(entity));
        when(reportMapper.toDomain(entity)).thenReturn(report);

        var result = reportRepositoryImpl.findByStatus(ReportStatus.PENDING);

        assertThat(result).hasSize(1).contains(report);
        verify(springDataReportRepository).findByStatus(ReportStatus.PENDING);
    }
}
