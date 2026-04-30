package com.hackaton.reportapi.infrastructure.db.repository;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.infrastructure.db.entity.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataReportRepository extends MongoRepository<ReportEntity, String> {
    List<ReportEntity> findByStatus(ReportStatus status);
}
