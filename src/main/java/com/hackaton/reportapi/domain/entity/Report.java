package com.hackaton.reportapi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private UUID id;
    private String title;
    private String description;
    private ReportStatus status;
    private String createdBy;
    private Map<String, Object> data;
    private String s3Key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
