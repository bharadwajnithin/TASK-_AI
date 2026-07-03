package com.taskflowai.controller;

import com.taskflowai.dto.email.EmailPageResponse;
import com.taskflowai.dto.email.ProcessEmailRequest;
import com.taskflowai.dto.email.ProcessEmailResponse;
import com.taskflowai.service.EmailService;
import com.taskflowai.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<EmailPageResponse> getEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(emailService.getEmails(userId, page, size));
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessEmailResponse> processEmail(@Valid @RequestBody ProcessEmailRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(emailService.processEmail(userId, request));
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncEmails(
            @RequestParam String fromEmail,
            @RequestParam(defaultValue = "20") int maxResults) {
        String userId = SecurityUtils.getCurrentUserId();
        EmailService.SyncResult result = emailService.syncGmailInbox(userId, maxResults, fromEmail);
        return ResponseEntity.ok(Map.of(
                "imported", result.imported(),
                "fromEmail", result.fromEmail(),
                "message", result.imported() + " new email(s) imported from " + result.fromEmail()));
    }
}
