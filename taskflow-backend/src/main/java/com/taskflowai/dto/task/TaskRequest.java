package com.taskflowai.dto.task;

import com.taskflowai.model.Priority;
import com.taskflowai.model.SourceType;
import com.taskflowai.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private Priority priority;

    private TaskStatus status;

    private LocalDate dueDate;

    private SourceType sourceType;

    @Size(max = 5000, message = "Source content must not exceed 5000 characters")
    private String sourceContent;

    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String clientName;

    @Size(max = 100, message = "Project name must not exceed 100 characters")
    private String projectName;
}
