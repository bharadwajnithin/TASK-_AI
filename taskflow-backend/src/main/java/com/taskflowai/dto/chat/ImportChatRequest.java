package com.taskflowai.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportChatRequest {

    @NotBlank(message = "Chat content is required")
    @Size(max = 500000, message = "Chat content must not exceed 500000 characters")
    private String content;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Builder.Default
    private boolean saveTasks = false;
}
