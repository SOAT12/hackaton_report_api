package com.hackaton.reportapi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private String id;
    private String diagramId;
    private String title;
    private ReportContent report;
    private ReportStatus status;
    private String reportUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
