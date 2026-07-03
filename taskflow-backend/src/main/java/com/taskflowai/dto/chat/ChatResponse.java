package com.taskflowai.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ChatResponse {

    private String id;
    private String title;
    private int messageCount;
    private List<String> participants;
    private boolean processed;
    private Instant importedAt;
    private String preview;
}
