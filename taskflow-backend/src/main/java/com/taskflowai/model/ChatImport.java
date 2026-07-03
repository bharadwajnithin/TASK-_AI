package com.taskflowai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_imports")
public class ChatImport {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;

    private String rawContent;

    private String formattedContent;

    private int messageCount;

    private List<String> participants;

    @Builder.Default
    private boolean processed = false;

    @CreatedDate
    private Instant importedAt;
}
