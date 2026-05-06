package com.hackaton.reportapi.domain.gateway;

import com.hackaton.reportapi.domain.event.ReportStatusEvent;

public interface EventPublisherGateway {
    void publish(ReportStatusEvent event);
}
