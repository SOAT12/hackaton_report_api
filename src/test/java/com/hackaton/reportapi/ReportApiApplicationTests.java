package com.hackaton.reportapi;

import com.hackaton.reportapi.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportApiApplicationTests {

    @Test
    void objectMapper_shouldHaveJavaTimeModuleAndNoTimestamps() {
        var config = new AppConfig();
        ObjectMapper mapper = config.objectMapper();

        assertThat(mapper).isNotNull();
        assertThat(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
    }
}
