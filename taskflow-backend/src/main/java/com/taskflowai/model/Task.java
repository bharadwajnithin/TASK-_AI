package com.taskflowai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
@CompoundIndex(name = "user_status_idx", def = "{'userId': 1, 'status': 1}")
public class Task {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;

    private String description;

    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    private LocalDate dueDate;

    @Builder.Default
    private SourceType sourceType = SourceType.MANUAL;

    private String sourceContent;

    private String clientName;

    private String projectName;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
