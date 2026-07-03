package com.taskflowai.util;

import com.taskflowai.exception.UnauthorizedException;
import com.taskflowai.model.User;
import com.taskflowai.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return details.getUser();
    }

    public static String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
