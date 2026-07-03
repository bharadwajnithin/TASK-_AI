package com.taskflowai.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessChatRequest {

    @NotBlank(message = "Chat ID is required")
    private String chatId;

    @Builder.Default
    private boolean saveTasks = true;
}
