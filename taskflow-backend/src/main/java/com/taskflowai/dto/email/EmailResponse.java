package com.taskflowai.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    private String id;
    private String sender;
    private String subject;
    private String body;
    private Instant receivedAt;
    private boolean processed;
    private Instant importedAt;
}
