package com.hackaton.reportapi.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "CreatedBy is required")
    private String createdBy;

    private Map<String, Object> data;
}
