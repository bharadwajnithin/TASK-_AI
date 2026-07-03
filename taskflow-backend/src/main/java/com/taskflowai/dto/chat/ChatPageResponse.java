package com.taskflowai.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatPageResponse {

    private List<ChatResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private long unprocessedCount;
}
