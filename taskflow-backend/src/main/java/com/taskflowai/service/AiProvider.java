package com.taskflowai.service;

import com.taskflowai.dto.ai.AiExtractionPayload;

public interface AiProvider {
    AiExtractionPayload extractTasks(String content);
    String getProviderName();
}
