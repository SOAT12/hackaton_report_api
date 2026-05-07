package com.hackaton.reportapi.infrastructure.controller;

import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.application.dto.UpdateReportStatusRequestDTO;
import com.hackaton.reportapi.application.usecase.CreateReportUseCase;
import com.hackaton.reportapi.application.usecase.GetReportUseCase;
import com.hackaton.reportapi.application.usecase.ListReportsUseCase;
import com.hackaton.reportapi.application.usecase.UpdateReportStatusUseCase;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report management endpoints")
public class ReportController {

    private final CreateReportUseCase createReportUseCase;
    private final GetReportUseCase getReportUseCase;
    private final ListReportsUseCase listReportsUseCase;
    private final UpdateReportStatusUseCase updateReportStatusUseCase;

    @PostMapping
    @Operation(summary = "Create a new report")
    public ResponseEntity<ReportResponseDTO> createReport(@Valid @RequestBody CreateReportRequestDTO request) {
        var response = createReportUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a report by ID")
    public ResponseEntity<ReportResponseDTO> getReport(@PathVariable String id) {
        var response = getReportUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all reports, optionally filtered by status")
    public ResponseEntity<List<ReportResponseDTO>> listReports(
            @Parameter(description = "Filter by report status")
            @RequestParam(required = false) ReportStatus status) {
        var reports = listReportsUseCase.execute(status);
        return ResponseEntity.ok(reports);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update report status")
    public ResponseEntity<ReportResponseDTO> updateReportStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateReportStatusRequestDTO request) {
        var response = updateReportStatusUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}
