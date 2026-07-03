package com.taskflowai.dto.email;

import com.taskflowai.dto.ai.ExtractResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessEmailResponse {

    private EmailResponse email;
    private ExtractResponse extraction;
}
