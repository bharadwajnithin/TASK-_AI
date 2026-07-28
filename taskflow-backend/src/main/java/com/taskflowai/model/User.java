package com.taskflowai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password;

    @Builder.Default
    private Role role = Role.USER;

    private String googleId;

    private String profilePictureUrl;

    @Builder.Default
    private boolean oauthUser = false;

    @Builder.Default
    private boolean gmailConnected = false;

    private String gmailRefreshToken;

    private String gmailAccessToken;

    private Instant gmailTokenExpiry;

    private String gmailSyncFromEmail;

    @Builder.Default
    private java.util.List<String> gmailSyncFromEmails = new java.util.ArrayList<>();

    public java.util.List<String> getGmailSyncFromEmailsList() {
        if (gmailSyncFromEmails != null && !gmailSyncFromEmails.isEmpty()) {
            return gmailSyncFromEmails;
        }
        if (org.springframework.util.StringUtils.hasText(gmailSyncFromEmail)) {
            return java.util.List.of(gmailSyncFromEmail);
        }
        return java.util.Collections.emptyList();
    }

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
