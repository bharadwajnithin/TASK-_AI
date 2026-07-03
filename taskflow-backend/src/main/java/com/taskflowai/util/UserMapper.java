package com.taskflowai.util;

import com.taskflowai.dto.user.UserResponse;
import com.taskflowai.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .profilePictureUrl(user.getProfilePictureUrl())
                .oauthUser(user.isOauthUser())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
