package com.taskflowai.controller;

import com.taskflowai.dto.email.GmailStatusResponse;
import com.taskflowai.service.GmailService;
import com.taskflowai.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gmail")
@RequiredArgsConstructor
public class GmailController {

    private final GmailService gmailService;

    @GetMapping("/status")
    public ResponseEntity<GmailStatusResponse> getStatus() {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(gmailService.getStatus(userId));
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<Void> disconnect() {
        String userId = SecurityUtils.getCurrentUserId();
        gmailService.disconnect(userId);
        return ResponseEntity.noContent().build();
    }
}
