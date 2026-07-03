package com.taskflowai.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiExtractionPayload {

    private String clientName;
    private String projectName;
    private List<AiTaskPayload> tasks = new ArrayList<>();

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiTaskPayload {
        private String title;
        private String description;
        private String priority;
        private String dueDate;
        private String dueDateText;
    }
}
