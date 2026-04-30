package com.hackaton.reportapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.hackaton.reportapi.infrastructure.db.repository")
public class ReportApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportApiApplication.class, args);
    }
}
