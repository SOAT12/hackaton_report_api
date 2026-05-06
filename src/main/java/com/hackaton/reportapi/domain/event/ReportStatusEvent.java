package com.hackaton.reportapi.domain.event;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatusEvent {
    private String reportId;
    private ReportStatus status;
    private String s3Key;
    private LocalDateTime updatedAt;
}
