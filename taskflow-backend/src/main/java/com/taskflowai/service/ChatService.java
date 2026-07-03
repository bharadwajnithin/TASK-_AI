package com.taskflowai.service;

import com.taskflowai.dto.ai.ExtractRequest;
import com.taskflowai.dto.ai.ExtractResponse;
import com.taskflowai.dto.chat.ChatPageResponse;
import com.taskflowai.dto.chat.ChatResponse;
import com.taskflowai.dto.chat.ImportChatRequest;
import com.taskflowai.dto.chat.ImportChatResponse;
import com.taskflowai.dto.chat.ProcessChatRequest;
import com.taskflowai.dto.chat.ProcessChatResponse;
import com.taskflowai.exception.ResourceNotFoundException;
import com.taskflowai.model.ChatImport;
import com.taskflowai.model.SourceType;
import com.taskflowai.repository.ChatImportRepository;
import com.taskflowai.util.WhatsAppChatParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_PREVIEW = 200;

    private final ChatImportRepository chatImportRepository;
    private final AiExtractionService aiExtractionService;

    public ImportChatResponse importChat(String userId, ImportChatRequest request) {
        WhatsAppChatParser.ParsedChat parsed =
                WhatsAppChatParser.parse(request.getContent(), request.getTitle());

        ChatImport chat = ChatImport.builder()
                .userId(userId)
                .title(parsed.getTitle())
                .rawContent(request.getContent().trim())
                .formattedContent(parsed.getFormattedContent())
                .messageCount(parsed.getMessageCount())
                .participants(parsed.getParticipants())
                .processed(false)
                .build();

        chat = chatImportRepository.save(chat);

        ExtractResponse extraction = null;
        if (request.isSaveTasks()) {
            extraction = processExtraction(userId, chat, true);
            chat.setProcessed(true);
            chat = chatImportRepository.save(chat);
        }

        return ImportChatResponse.builder()
                .chat(toResponse(chat))
                .extraction(extraction)
                .message("Imported " + chat.getMessageCount() + " message(s) from WhatsApp chat")
                .build();
    }

    public ChatPageResponse getChats(String userId, int page, int size) {
        Page<ChatImport> result = chatImportRepository.findByUserIdOrderByImportedAtDesc(
                userId, PageRequest.of(Math.max(page, 0), clampSize(size)));

        List<ChatResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();

        return ChatPageResponse.builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .unprocessedCount(chatImportRepository.countByUserIdAndProcessedFalse(userId))
                .build();
    }

    public ProcessChatResponse processChat(String userId, ProcessChatRequest request) {
        ChatImport chat = chatImportRepository.findByIdAndUserId(request.getChatId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat import not found: " + request.getChatId()));

        ExtractResponse extraction = processExtraction(userId, chat, request.isSaveTasks());

        chat.setProcessed(true);
        chat = chatImportRepository.save(chat);

        return ProcessChatResponse.builder()
                .chat(toResponse(chat))
                .extraction(extraction)
                .build();
    }

    public void deleteChat(String userId, String chatId) {
        ChatImport chat = chatImportRepository.findByIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat import not found: " + chatId));
        chatImportRepository.delete(chat);
    }

    private ExtractResponse processExtraction(String userId, ChatImport chat, boolean saveTasks) {
        String content = buildAiContent(chat);
        return aiExtractionService.extract(userId, ExtractRequest.builder()
                .content(content)
                .saveTasks(saveTasks)
                .sourceType(SourceType.WHATSAPP)
                .build());
    }

    private String buildAiContent(ChatImport chat) {
        StringBuilder sb = new StringBuilder();
        sb.append("WhatsApp chat: ").append(chat.getTitle()).append('\n');
        if (chat.getParticipants() != null && !chat.getParticipants().isEmpty()) {
            sb.append("Participants: ").append(String.join(", ", chat.getParticipants())).append('\n');
        }
        sb.append('\n').append(chat.getFormattedContent());
        return sb.toString();
    }

    private ChatResponse toResponse(ChatImport chat) {
        String preview = chat.getFormattedContent();
        if (StringUtils.hasText(preview) && preview.length() > MAX_PREVIEW) {
            preview = preview.substring(0, MAX_PREVIEW) + "...";
        }

        return ChatResponse.builder()
                .id(chat.getId())
                .title(chat.getTitle())
                .messageCount(chat.getMessageCount())
                .participants(chat.getParticipants())
                .processed(chat.isProcessed())
                .importedAt(chat.getImportedAt())
                .preview(preview)
                .build();
    }

    private int clampSize(int size) {
        return Math.min(Math.max(size, 1), 50);
    }
}
