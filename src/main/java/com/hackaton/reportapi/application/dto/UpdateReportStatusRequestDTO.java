package com.hackaton.reportapi.application.dto;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportStatusRequestDTO {

    @NotNull(message = "Status is required")
    private ReportStatus status;
}
