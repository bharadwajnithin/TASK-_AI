package com.taskflowai.controller;

import com.taskflowai.dto.chat.ChatPageResponse;
import com.taskflowai.dto.chat.ImportChatRequest;
import com.taskflowai.dto.chat.ImportChatResponse;
import com.taskflowai.dto.chat.ProcessChatRequest;
import com.taskflowai.dto.chat.ProcessChatResponse;
import com.taskflowai.service.ChatService;
import com.taskflowai.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/import")
    public ResponseEntity<ImportChatResponse> importChat(@Valid @RequestBody ImportChatRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.importChat(userId, request));
    }

    @GetMapping
    public ResponseEntity<ChatPageResponse> getChats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(chatService.getChats(userId, page, size));
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessChatResponse> processChat(@Valid @RequestBody ProcessChatRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(chatService.processChat(userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId();
        chatService.deleteChat(userId, id);
        return ResponseEntity.noContent().build();
    }
}
