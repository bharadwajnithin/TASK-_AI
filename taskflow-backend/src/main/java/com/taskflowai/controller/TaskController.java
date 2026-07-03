package com.taskflowai.controller;

import com.taskflowai.dto.task.TaskPageResponse;
import com.taskflowai.dto.task.TaskRequest;
import com.taskflowai.dto.task.TaskResponse;
import com.taskflowai.dto.task.TaskStatsResponse;
import com.taskflowai.model.Priority;
import com.taskflowai.model.TaskStatus;
import com.taskflowai.service.TaskService;
import com.taskflowai.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        TaskResponse response = taskService.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<TaskPageResponse> getTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                taskService.getTasks(userId, search, status, priority, page, size, sortBy, sortDir));
    }

    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponse> getTaskStats() {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.getTaskStats(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.getTaskById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.updateTask(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId();
        taskService.deleteTask(userId, id);
        return ResponseEntity.noContent().build();
    }
}
