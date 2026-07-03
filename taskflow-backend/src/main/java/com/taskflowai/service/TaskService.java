package com.taskflowai.service;

import com.taskflowai.dto.task.TaskPageResponse;
import com.taskflowai.dto.task.TaskRequest;
import com.taskflowai.dto.task.TaskResponse;
import com.taskflowai.dto.task.TaskStatsResponse;
import com.taskflowai.exception.ResourceNotFoundException;
import com.taskflowai.model.Priority;
import com.taskflowai.model.SourceType;
import com.taskflowai.model.Task;
import com.taskflowai.model.TaskStatus;
import com.taskflowai.repository.TaskRepository;
import com.taskflowai.util.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    public TaskResponse createTask(String userId, TaskRequest request) {
        Task task = Task.builder()
                .userId(userId)
                .title(request.getTitle().trim())
                .description(trimToNull(request.getDescription()))
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING)
                .dueDate(request.getDueDate())
                .sourceType(request.getSourceType() != null ? request.getSourceType() : SourceType.MANUAL)
                .sourceContent(trimToNull(request.getSourceContent()))
                .clientName(trimToNull(request.getClientName()))
                .projectName(trimToNull(request.getProjectName()))
                .build();

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    public TaskPageResponse getTasks(
            String userId,
            String search,
            TaskStatus status,
            Priority priority,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Query query = buildFilterQuery(userId, search, status, priority);
        long total = mongoTemplate.count(query, Task.class);

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                mapSortField(sortBy));
        Pageable pageable = PageRequest.of(Math.max(page, 0), clampSize(size), sort);

        query.with(pageable);
        List<TaskResponse> content = mongoTemplate.find(query, Task.class).stream()
                .map(TaskMapper::toResponse)
                .toList();

        int totalPages = size > 0 ? (int) Math.ceil((double) total / clampSize(size)) : 0;

        return TaskPageResponse.builder()
                .content(content)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(total)
                .totalPages(totalPages)
                .first(pageable.getPageNumber() == 0)
                .last(pageable.getPageNumber() >= totalPages - 1 || totalPages == 0)
                .build();
    }

    public TaskResponse getTaskById(String userId, String taskId) {
        Task task = findOwnedTask(userId, taskId);
        return TaskMapper.toResponse(task);
    }

    public TaskResponse updateTask(String userId, String taskId, TaskRequest request) {
        Task task = findOwnedTask(userId, taskId);

        task.setTitle(request.getTitle().trim());
        task.setDescription(trimToNull(request.getDescription()));
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        task.setDueDate(request.getDueDate());
        if (request.getSourceType() != null) {
            task.setSourceType(request.getSourceType());
        }
        task.setSourceContent(trimToNull(request.getSourceContent()));
        task.setClientName(trimToNull(request.getClientName()));
        task.setProjectName(trimToNull(request.getProjectName()));

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(String userId, String taskId) {
        Task task = findOwnedTask(userId, taskId);
        taskRepository.delete(task);
    }

    public TaskStatsResponse getTaskStats(String userId) {
        long total = taskRepository.countByUserId(userId);
        long pending = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
        long inProgress = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        long completed = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        long overdue = taskRepository.countByUserIdAndStatusNotAndDueDateBefore(
                userId, TaskStatus.COMPLETED, LocalDate.now());

        List<TaskResponse> recent = taskRepository.findTop5ByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(TaskMapper::toResponse)
                .toList();

        return TaskStatsResponse.builder()
                .totalTasks(total)
                .pendingTasks(pending)
                .inProgressTasks(inProgress)
                .completedTasks(completed)
                .overdueTasks(overdue)
                .recentTasks(recent)
                .build();
    }

    private Task findOwnedTask(String userId, String taskId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }

    private Query buildFilterQuery(String userId, String search, TaskStatus status, Priority priority) {
        Criteria base = Criteria.where("userId").is(userId);

        if (status != null) {
            base = base.and("status").is(status);
        }
        if (priority != null) {
            base = base.and("priority").is(priority);
        }

        if (StringUtils.hasText(search)) {
            String escaped = Pattern.quote(search.trim());
            Pattern pattern = Pattern.compile(escaped, Pattern.CASE_INSENSITIVE);
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(pattern),
                    Criteria.where("description").regex(pattern),
                    Criteria.where("clientName").regex(pattern),
                    Criteria.where("projectName").regex(pattern));
            return new Query(new Criteria().andOperator(base, searchCriteria));
        }

        return new Query(base);
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy != null ? sortBy : "") {
            case "title" -> "title";
            case "priority" -> "priority";
            case "status" -> "status";
            case "dueDate" -> "dueDate";
            case "createdAt" -> "createdAt";
            default -> "updatedAt";
        };
    }

    private int clampSize(int size) {
        if (size < 1) return 10;
        return Math.min(size, 100);
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
