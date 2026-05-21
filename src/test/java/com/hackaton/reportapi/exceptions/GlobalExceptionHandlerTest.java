package com.hackaton.reportapi.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFound_shouldReturn404WithMessage() {
        var ex = new ResourceNotFoundException("Report not found with id: abc");

        var response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Report not found with id: abc");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("createReportRequestDTO", "diagramId", "must not be null");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        var ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("Validation failed");
        assertThat(response.getBody().message()).contains("diagramId");
    }

    @Test
    void handleValidation_shouldIncludeAllFieldErrors() {
        var bindingResult = mock(BindingResult.class);
        var error1 = new FieldError("obj", "diagramId", "must not be null");
        var error2 = new FieldError("obj", "report", "must not be null");
        when(bindingResult.getAllErrors()).thenReturn(List.of(error1, error2));

        var ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handleValidation(ex);

        assertThat(response.getBody().message()).contains("diagramId");
        assertThat(response.getBody().message()).contains("report");
    }

    @Test
    void handleGeneric_shouldReturn500WithGenericMessage() {
        var ex = new RuntimeException("something went wrong");

        var response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void errorResponse_shouldStoreAllValues() {
        var now = java.time.LocalDateTime.now();
        var errorResponse = new GlobalExceptionHandler.ErrorResponse(404, "not found", now);

        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.message()).isEqualTo("not found");
        assertThat(errorResponse.timestamp()).isEqualTo(now);
    }
}
