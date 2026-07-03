package com.taskflowai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "emails")
@CompoundIndex(name = "user_gmail_msg_idx", def = "{'userId': 1, 'gmailMessageId': 1}", unique = true)
public class EmailMessage {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String gmailMessageId;

    private String sender;

    private String subject;

    private String body;

    private Instant receivedAt;

    @Builder.Default
    private boolean processed = false;

    @CreatedDate
    private Instant importedAt;
}
