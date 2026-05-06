package com.hackaton.reportapi.application.dto;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.domain.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private String id;
    private String title;
    private String description;
    private ReportType type;
    private ReportStatus status;
    private String createdBy;
    private Map<String, Object> data;
    private String s3Key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
