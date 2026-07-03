package com.taskflowai.dto.email;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessEmailRequest {

    @NotBlank(message = "Email id is required")
    private String emailId;

    @Builder.Default
    private boolean saveTasks = true;
}
