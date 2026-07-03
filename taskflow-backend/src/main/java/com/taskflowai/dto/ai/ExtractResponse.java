package com.taskflowai.dto.ai;

import com.taskflowai.dto.task.TaskResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractResponse {

    private String clientName;
    private String projectName;
    @Builder.Default
    private List<ExtractedTaskDto> tasks = new ArrayList<>();
    @Builder.Default
    private List<TaskResponse> savedTasks = new ArrayList<>();
}
