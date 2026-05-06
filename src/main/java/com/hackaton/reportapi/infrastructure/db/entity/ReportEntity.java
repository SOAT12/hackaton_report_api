package com.hackaton.reportapi.infrastructure.db.entity;

import com.hackaton.reportapi.domain.entity.ReportStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class ReportEntity {

    @Id
    private UUID id;

    private String title;
    private String description;

    @Indexed
    private ReportStatus status;

    private String createdBy;
    private Map<String, Object> data;
    private String s3Key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
