package com.taskflowai.dto.chat;

import com.taskflowai.dto.ai.ExtractResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessChatResponse {

    private ChatResponse chat;
    private ExtractResponse extraction;
}
