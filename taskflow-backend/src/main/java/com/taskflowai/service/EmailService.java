package com.taskflowai.service;

import com.taskflowai.dto.ai.ExtractRequest;
import com.taskflowai.dto.ai.ExtractResponse;
import com.taskflowai.dto.email.EmailPageResponse;
import com.taskflowai.dto.email.EmailResponse;
import com.taskflowai.dto.email.ProcessEmailRequest;
import com.taskflowai.dto.email.ProcessEmailResponse;
import com.taskflowai.exception.ResourceNotFoundException;
import com.taskflowai.model.EmailMessage;
import com.taskflowai.model.SourceType;
import com.taskflowai.model.User;
import com.taskflowai.repository.EmailMessageRepository;
import com.taskflowai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailMessageRepository emailMessageRepository;
    private final UserRepository userRepository;
    private final GmailApiClient gmailApiClient;
    private final AiExtractionService aiExtractionService;

    public EmailPageResponse getEmails(String userId, int page, int size) {
        Page<EmailMessage> result = emailMessageRepository.findByUserIdOrderByReceivedAtDesc(
                userId, PageRequest.of(Math.max(page, 0), clampSize(size)));

        List<EmailResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();

        return EmailPageResponse.builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    public SyncResult syncGmailInbox(String userId, int maxResults, String fromEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String normalizedFromEmail = normalizeFromEmail(fromEmail);
        user.setGmailSyncFromEmail(normalizedFromEmail);
        userRepository.save(user);

        List<EmailMessage> fetched =
                gmailApiClient.fetchRecentInboxMessages(user, maxResults, normalizedFromEmail);
        int imported = 0;

        for (EmailMessage email : fetched) {
            if (!emailMessageRepository.existsByUserIdAndGmailMessageId(userId, email.getGmailMessageId())) {
                emailMessageRepository.save(email);
                imported++;
            }
        }

        return new SyncResult(imported, normalizedFromEmail);
    }

    private String normalizeFromEmail(String fromEmail) {
        if (!StringUtils.hasText(fromEmail)) {
            throw new IllegalArgumentException("Sender email address is required to sync inbox");
        }
        String trimmed = fromEmail.trim().toLowerCase();
        if (!trimmed.matches("^[a-z0-9._%+\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid sender email address");
        }
        return trimmed;
    }

    public record SyncResult(int imported, String fromEmail) {}

    public ProcessEmailResponse processEmail(String userId, ProcessEmailRequest request) {
        EmailMessage email = emailMessageRepository.findByIdAndUserId(request.getEmailId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found: " + request.getEmailId()));

        String content = buildAiContent(email);
        ExtractResponse extraction = aiExtractionService.extract(userId, ExtractRequest.builder()
                .content(content)
                .saveTasks(request.isSaveTasks())
                .sourceType(SourceType.EMAIL)
                .build());

        email.setProcessed(true);
        emailMessageRepository.save(email);

        return ProcessEmailResponse.builder()
                .email(toResponse(email))
                .extraction(extraction)
                .build();
    }

    private String buildAiContent(EmailMessage email) {
        return """
                Email from: %s
                Subject: %s

                %s
                """.formatted(
                email.getSender() != null ? email.getSender() : "Unknown",
                email.getSubject() != null ? email.getSubject() : "No subject",
                email.getBody() != null ? email.getBody() : "");
    }

    private EmailResponse toResponse(EmailMessage email) {
        return EmailResponse.builder()
                .id(email.getId())
                .sender(email.getSender())
                .subject(email.getSubject())
                .body(email.getBody())
                .receivedAt(email.getReceivedAt())
                .processed(email.isProcessed())
                .importedAt(email.getImportedAt())
                .build();
    }

    private int clampSize(int size) {
        if (size < 1) return 10;
        return Math.min(size, 50);
    }
}
