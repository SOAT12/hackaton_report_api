package com.hackaton.reportapi.domain.gateway;

import com.hackaton.reportapi.domain.entity.Report;
import com.hackaton.reportapi.domain.entity.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findById(String id);
    List<Report> findAll();
    List<Report> findByStatus(ReportStatus status);
}
