package com.taskflowai.service;

import com.taskflowai.config.AppProperties;
import com.taskflowai.dto.email.GmailStatusResponse;
import com.taskflowai.exception.ResourceNotFoundException;
import com.taskflowai.model.User;
import com.taskflowai.repository.EmailMessageRepository;
import com.taskflowai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailService {

    private final UserRepository userRepository;
    private final EmailMessageRepository emailMessageRepository;
    private final GmailTokenService gmailTokenService;
    private final AppProperties appProperties;

    public GmailStatusResponse getStatus(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String connectUrl = appProperties.getOauth().isGoogleEnabled()
                ? "/oauth2/authorization/google-gmail"
                : null;

        return GmailStatusResponse.builder()
                .connected(user.isGmailConnected())
                .accountEmail(user.getEmail())
                .unprocessedCount(emailMessageRepository.countByUserIdAndProcessedFalse(userId))
                .connectUrl(connectUrl)
                .syncFromEmail(user.getGmailSyncFromEmail())
                .build();
    }

    public void disconnect(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        gmailTokenService.disconnect(user);
    }
}
