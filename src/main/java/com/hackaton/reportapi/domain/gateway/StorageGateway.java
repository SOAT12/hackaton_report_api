package com.hackaton.reportapi.domain.gateway;

public interface StorageGateway {
    String upload(String key, String content);
}
