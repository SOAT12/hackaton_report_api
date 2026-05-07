package com.hackaton.reportapi.application.dto;

import com.hackaton.reportapi.domain.entity.ReportContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequestDTO {

    @NotNull(message = "DiagramId is required")
    private UUID diagramId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Report content is required")
    private ReportContent report;
}
