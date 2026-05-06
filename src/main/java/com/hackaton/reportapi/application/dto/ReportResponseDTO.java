package com.hackaton.reportapi.application.dto;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import lombok.*;

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
    private ReportStatus status;
    private String createdBy;
    private Map<String, Object> data;
    private String s3Key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
