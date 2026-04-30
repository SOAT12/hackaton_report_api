package com.hackaton.reportapi.infrastructure.db.gateway;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.gateway.ReportRepository;
import com.hackaton.reportapi.infrastructure.db.mapper.ReportMapper;
import com.hackaton.reportapi.infrastructure.db.repository.SpringDataReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final SpringDataReportRepository springDataReportRepository;
    private final ReportMapper reportMapper;

    @Override
    public Report save(Report report) {
        var entity = reportMapper.toEntity(report);
        var saved = springDataReportRepository.save(entity);
        return reportMapper.toDomain(saved);
    }

    @Override
    public Optional<Report> findById(String id) {
        return springDataReportRepository.findById(id)
                .map(reportMapper::toDomain);
    }

    @Override
    public List<Report> findAll() {
        return springDataReportRepository.findAll()
                .stream()
                .map(reportMapper::toDomain)
                .toList();
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return springDataReportRepository.findByStatus(status)
                .stream()
                .map(reportMapper::toDomain)
                .toList();
    }
}
