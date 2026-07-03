package com.taskflowai.service;

import com.taskflowai.dto.analytics.AnalyticsResponse;
import com.taskflowai.model.Priority;
import com.taskflowai.model.SourceType;
import com.taskflowai.model.Task;
import com.taskflowai.model.TaskStatus;
import com.taskflowai.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    public AnalyticsResponse getAnalytics(String userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        
        // Count overdue tasks
        long overdueTasks = taskRepository.countByUserIdAndStatusNotAndDueDateBefore(
                userId, TaskStatus.COMPLETED, LocalDate.now());
        long onTimeTasks = totalTasks - overdueTasks;

        // Get tasks by status
        Map<String, Long> tasksByStatus = getTasksByStatus(userId);
        
        // Get tasks by priority
        Map<String, Long> tasksByPriority = getTasksByPriority(userId);
        
        // Get tasks by source
        Map<String, Long> tasksBySource = getTasksBySource(userId);
        
        // Calculate completion rate
        double completionRate = totalTasks > 0 
                ? (tasksByStatus.getOrDefault("COMPLETED", 0L) * 100.0 / totalTasks) 
                : 0.0;
        
        // Get trends for last 7 days
        Map<String, Long> tasksCreatedLast7Days = getTasksCreatedLast7Days(userId);
        Map<String, Long> tasksCompletedLast7Days = getTasksCompletedLast7Days(userId);

        return AnalyticsResponse.builder()
                .totalTasks(totalTasks)
                .overdueTasks(overdueTasks)
                .onTimeTasks(onTimeTasks)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksBySource(tasksBySource)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .tasksCreatedLast7Days(tasksCreatedLast7Days)
                .tasksCompletedLast7Days(tasksCompletedLast7Days)
                .build();
    }

    private Map<String, Long> getTasksByStatus(String userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            long count = taskRepository.countByUserIdAndStatus(userId, status);
            result.put(status.name(), count);
        }
        return result;
    }

    private Map<String, Long> getTasksByPriority(String userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Priority priority : Priority.values()) {
            long count = taskRepository.countByUserIdAndPriority(userId, priority);
            result.put(priority.name(), count);
        }
        return result;
    }

    private Map<String, Long> getTasksBySource(String userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (SourceType source : SourceType.values()) {
            long count = taskRepository.countByUserIdAndSourceType(userId, source);
            result.put(source.name(), count);
        }
        return result;
    }

    private Map<String, Long> getTasksCreatedLast7Days(String userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            
            long count = taskRepository.countByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
            result.put(date.format(formatter), count);
        }
        return result;
    }

    private Map<String, Long> getTasksCompletedLast7Days(String userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            
            long count = taskRepository.countByUserIdAndStatusAndUpdatedAtBetween(
                    userId, TaskStatus.COMPLETED, startOfDay, endOfDay);
            result.put(date.format(formatter), count);
        }
        return result;
    }
}
