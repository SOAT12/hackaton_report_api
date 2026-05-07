package com.hackaton.reportapi.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hackaton.reportapi.application.dto.CreateReportRequestDTO;
import com.hackaton.reportapi.application.dto.ReportResponseDTO;
import com.hackaton.reportapi.application.dto.UpdateReportStatusRequestDTO;
import com.hackaton.reportapi.application.usecase.CreateReportUseCase;
import com.hackaton.reportapi.application.usecase.GetReportUseCase;
import com.hackaton.reportapi.application.usecase.ListReportsUseCase;
import com.hackaton.reportapi.application.usecase.UpdateReportStatusUseCase;
import com.hackaton.reportapi.domain.entity.ReportContent;
import com.hackaton.reportapi.domain.entity.ReportStatus;
import com.hackaton.reportapi.exceptions.GlobalExceptionHandler;
import com.hackaton.reportapi.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private CreateReportUseCase createReportUseCase;

    @Mock
    private GetReportUseCase getReportUseCase;

    @Mock
    private ListReportsUseCase listReportsUseCase;

    @Mock
    private UpdateReportStatusUseCase updateReportStatusUseCase;

    @InjectMocks
    private ReportController reportController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReportResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleResponse = ReportResponseDTO.builder()
                .id("report-id-1")
                .diagramId("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55")
                .title("Architecture Report")
                .report(ReportContent.builder()
                        .components(List.of("component_01"))
                        .risks(List.of("risk_01"))
                        .recommendations(List.of("rec_01"))
                        .build())
                .status(ReportStatus.COMPLETED)
                .reportUrl("http://localhost:8080/api/reports/report-id-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createReport_shouldReturn201WithBody() throws Exception {
        var request = CreateReportRequestDTO.builder()
                .diagramId(UUID.fromString("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55"))
                .title("Architecture Report")
                .report(ReportContent.builder()
                        .components(List.of("component_01"))
                        .risks(List.of("risk_01"))
                        .recommendations(List.of("rec_01"))
                        .build())
                .build();

        when(createReportUseCase.execute(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("report-id-1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.diagramId").value("3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55"));
    }

    @Test
    void createReport_withInvalidRequest_shouldReturn400() throws Exception {
        var invalidRequest = CreateReportRequestDTO.builder()
                .title("missing diagramId and report content")
                .build();

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReport_shouldReturn200WhenFound() throws Exception {
        when(getReportUseCase.execute("report-id-1")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/reports/report-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("report-id-1"))
                .andExpect(jsonPath("$.title").value("Architecture Report"));
    }

    @Test
    void getReport_shouldReturn404WhenNotFound() throws Exception {
        when(getReportUseCase.execute("missing")).thenThrow(new ResourceNotFoundException("Report not found with id: missing"));

        mockMvc.perform(get("/api/reports/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Report not found with id: missing"));
    }

    @Test
    void listReports_withNoFilter_shouldReturn200() throws Exception {
        when(listReportsUseCase.execute(null)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("report-id-1"));
    }

    @Test
    void listReports_withStatusFilter_shouldReturn200() throws Exception {
        when(listReportsUseCase.execute(ReportStatus.COMPLETED)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/reports").param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void updateReportStatus_shouldReturn200() throws Exception {
        var updatedResponse = ReportResponseDTO.builder()
                .id("report-id-1")
                .status(ReportStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var request = new UpdateReportStatusRequestDTO(ReportStatus.FAILED);
        when(updateReportStatusUseCase.execute(eq("report-id-1"), any())).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/reports/report-id-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    @Test
    void updateReportStatus_shouldReturn404WhenNotFound() throws Exception {
        var request = new UpdateReportStatusRequestDTO(ReportStatus.FAILED);
        when(updateReportStatusUseCase.execute(eq("missing"), any()))
                .thenThrow(new ResourceNotFoundException("Report not found with id: missing"));

        mockMvc.perform(patch("/api/reports/missing/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
