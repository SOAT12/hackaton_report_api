package com.hackaton.reportapi.infrastructure.db.entity;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class ReportEntity {

    @Id
    private String id;

    private String title;
    private String description;
    private ReportType type;

    @Indexed
    private ReportStatus status;

    private String createdBy;
    private Map<String, Object> data;
    private String s3Key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
