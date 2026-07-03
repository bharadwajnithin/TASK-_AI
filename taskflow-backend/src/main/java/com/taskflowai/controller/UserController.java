package com.taskflowai.controller;

import com.taskflowai.dto.user.UserResponse;
import com.taskflowai.service.UserService;
import com.taskflowai.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
