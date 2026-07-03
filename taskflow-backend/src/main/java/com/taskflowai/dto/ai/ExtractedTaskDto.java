package com.taskflowai.dto.ai;

import com.taskflowai.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedTaskDto {

    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private String dueDateText;
}
