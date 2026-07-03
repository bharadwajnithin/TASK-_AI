package com.taskflowai.dto.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmailListResponse {

    private List<GmailMessageRef> messages;
    private String nextPageToken;
    private int resultSizeEstimate;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GmailMessageRef {
        private String id;
        private String threadId;
    }
}
