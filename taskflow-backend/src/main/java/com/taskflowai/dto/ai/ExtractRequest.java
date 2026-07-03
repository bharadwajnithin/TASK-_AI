package com.taskflowai.dto.ai;

import com.taskflowai.model.SourceType;
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
public class ExtractRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 20000, message = "Content must not exceed 20000 characters")
    private String content;

    @Builder.Default
    private boolean saveTasks = false;

    private SourceType sourceType;
}
