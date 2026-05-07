package com.hackaton.reportapi.infrastructure.db.entity;

import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class ReportEntity {

    @Id
    private String id;

    @Indexed
    private String diagramId;

    private String title;
    private ReportContent report;

    @Indexed
    private ReportStatus status;

    private String reportUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
