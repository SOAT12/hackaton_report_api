package com.hackaton.reportapi.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        var ex = new ResourceNotFoundException("Report not found with id: 123");

        assertThat(ex.getMessage()).isEqualTo("Report not found with id: 123");
    }

    @Test
    void shouldBeRuntimeException() {
        var ex = new ResourceNotFoundException("not found");

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
