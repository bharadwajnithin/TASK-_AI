package com.taskflowai.dto.chat;

import com.taskflowai.dto.ai.ExtractResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportChatResponse {

    private ChatResponse chat;
    private ExtractResponse extraction;
    private String message;
}
