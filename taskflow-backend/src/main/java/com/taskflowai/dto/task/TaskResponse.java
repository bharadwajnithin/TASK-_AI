package com.taskflowai.dto.task;

import com.taskflowai.model.Priority;
import com.taskflowai.model.SourceType;
import com.taskflowai.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private LocalDate dueDate;
    private SourceType sourceType;
    private String sourceContent;
    private String clientName;
    private String projectName;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean overdue;
}
