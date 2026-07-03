package com.taskflowai.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private long totalTasks;
    private long overdueTasks;
    private long onTimeTasks;
    
    // Task counts by status
    private Map<String, Long> tasksByStatus;
    
    // Task counts by priority
    private Map<String, Long> tasksByPriority;
    
    // Task counts by source
    private Map<String, Long> tasksBySource;
    
    // Completion rate
    private double completionRate;
    
    // Trends over time (last 7 days)
    private Map<String, Long> tasksCreatedLast7Days;
    private Map<String, Long> tasksCompletedLast7Days;
}
