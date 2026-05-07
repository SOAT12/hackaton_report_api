package com.hackaton.reportapi.domain.event;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatusEvent {
    private String diagramId;
    private ReportStatus status;
    private String reportLink;
    private String notes;
}
