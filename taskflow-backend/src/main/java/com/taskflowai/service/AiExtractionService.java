package com.taskflowai.service;

import com.taskflowai.dto.ai.AiExtractionPayload;
import com.taskflowai.dto.ai.ExtractRequest;
import com.taskflowai.dto.ai.ExtractResponse;
import com.taskflowai.dto.ai.ExtractedTaskDto;
import com.taskflowai.dto.task.TaskRequest;
import com.taskflowai.dto.task.TaskResponse;
import com.taskflowai.model.Priority;
import com.taskflowai.model.SourceType;
import com.taskflowai.util.DateParseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiExtractionService {

    private final AiProviderFactory aiProviderFactory;
    private final TaskService taskService;

    public ExtractResponse extract(String userId, ExtractRequest request) {
        AiProvider aiProvider = aiProviderFactory.getProvider();
        AiExtractionPayload payload = aiProvider.extractTasks(request.getContent());

        String clientName = payload.getClientName();
        String projectName = payload.getProjectName();

        List<ExtractedTaskDto> extracted = new ArrayList<>();
        List<TaskResponse> saved = new ArrayList<>();

        if (payload.getTasks() != null) {
            for (AiExtractionPayload.AiTaskPayload aiTask : payload.getTasks()) {
                if (!StringUtils.hasText(aiTask.getTitle())) {
                    continue;
                }

                Priority priority = parsePriority(aiTask.getPriority());
                var dueDate = DateParseUtil.parseFlexibleDate(aiTask.getDueDate());
                if (dueDate == null && StringUtils.hasText(aiTask.getDueDateText())) {
                    dueDate = DateParseUtil.parseFlexibleDate(aiTask.getDueDateText());
                }

                ExtractedTaskDto dto = ExtractedTaskDto.builder()
                        .title(aiTask.getTitle().trim())
                        .description(trim(aiTask.getDescription()))
                        .priority(priority)
                        .dueDate(dueDate)
                        .dueDateText(aiTask.getDueDateText())
                        .build();
                extracted.add(dto);

                if (request.isSaveTasks()) {
                    TaskRequest taskRequest = TaskRequest.builder()
                            .title(dto.getTitle())
                            .description(dto.getDescription())
                            .priority(dto.getPriority())
                            .dueDate(dto.getDueDate())
                            .sourceType(request.getSourceType() != null
                                    ? request.getSourceType() : SourceType.MANUAL)
                            .sourceContent(trim(request.getContent()))
                            .clientName(clientName)
                            .projectName(projectName)
                            .build();
                    saved.add(taskService.createTask(userId, taskRequest));
                }
            }
        }

        return ExtractResponse.builder()
                .clientName(clientName)
                .projectName(projectName)
                .tasks(extracted)
                .savedTasks(saved)
                .build();
    }

    private Priority parsePriority(String value) {
        if (!StringUtils.hasText(value)) {
            return Priority.MEDIUM;
        }
        try {
            return Priority.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Priority.MEDIUM;
        }
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
