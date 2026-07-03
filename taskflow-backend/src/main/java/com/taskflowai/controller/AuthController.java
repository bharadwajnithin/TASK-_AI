package com.taskflowai.controller;

import com.taskflowai.config.AppProperties;
import com.taskflowai.dto.auth.AuthResponse;
import com.taskflowai.dto.auth.LoginRequest;
import com.taskflowai.dto.auth.RegisterRequest;
import com.taskflowai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppProperties appProperties;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/google")
    public ResponseEntity<Map<String, Object>> googleOAuthInfo() {
        boolean enabled = appProperties.getOauth().isGoogleEnabled()
                && StringUtils.hasText(appProperties.getOauth().getGoogleClientId())
                && StringUtils.hasText(appProperties.getOauth().getGoogleClientSecret());

        String message = enabled
                ? "Google OAuth is ready"
                : "Google OAuth is disabled. Set GOOGLE_OAUTH_ENABLED=true and GOOGLE_CLIENT_ID/SECRET in backend .env, then restart with .\\run.ps1";

        return ResponseEntity.ok(Map.of(
                "enabled", enabled,
                "url", "/oauth2/authorization/google",
                "message", message));
    }
}
