package com.taskflowai.util;

import com.taskflowai.dto.task.TaskResponse;
import com.taskflowai.model.Task;
import com.taskflowai.model.TaskStatus;

import java.time.LocalDate;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .sourceType(task.getSourceType())
                .sourceContent(task.getSourceContent())
                .clientName(task.getClientName())
                .projectName(task.getProjectName())
                .userId(task.getUserId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .overdue(isOverdue(task))
                .build();
    }

    public static boolean isOverdue(Task task) {
        if (task.getDueDate() == null || task.getStatus() == TaskStatus.COMPLETED) {
            return false;
        }
        return task.getDueDate().isBefore(LocalDate.now());
    }
}
