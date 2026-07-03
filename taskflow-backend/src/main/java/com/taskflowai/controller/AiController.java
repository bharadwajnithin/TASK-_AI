package com.taskflowai.controller;

import com.taskflowai.dto.ai.ExtractRequest;
import com.taskflowai.dto.ai.ExtractResponse;
import com.taskflowai.service.AiExtractionService;
import com.taskflowai.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiExtractionService aiExtractionService;

    @PostMapping("/extract")
    public ResponseEntity<ExtractResponse> extractTasks(@Valid @RequestBody ExtractRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(aiExtractionService.extract(userId, request));
    }
}
