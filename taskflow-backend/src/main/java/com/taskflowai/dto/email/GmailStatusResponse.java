package com.taskflowai.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GmailStatusResponse {

    private boolean connected;
    private String accountEmail;
    private long unprocessedCount;
    private String connectUrl;
    private String syncFromEmail;
    private List<String> syncFromEmails;
}
