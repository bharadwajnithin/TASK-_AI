package com.taskflowai.dto.user;

import com.taskflowai.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private Role role;
    private String profilePictureUrl;
    private boolean oauthUser;
    private Instant createdAt;
}
