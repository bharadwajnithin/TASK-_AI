package com.taskflowai.service;

import com.taskflowai.dto.gmail.GmailListResponse;
import com.taskflowai.dto.gmail.GmailMessageDetail;
import com.taskflowai.exception.AiServiceException;
import com.taskflowai.model.EmailMessage;
import com.taskflowai.model.User;
import com.taskflowai.util.GmailMessageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailApiClient {

    private static final String GMAIL_BASE = "https://gmail.googleapis.com/gmail/v1";

    private final GmailTokenService gmailTokenService;
    private final RestClient.Builder restClientBuilder;

    public List<EmailMessage> fetchRecentInboxMessages(User user, int maxResults, String fromEmail) {
        String accessToken = gmailTokenService.getValidAccessToken(user);
        String query = "in:inbox from:" + sanitizeFromEmail(fromEmail);

        try {
            RestClient client = restClientBuilder
                    .baseUrl(GMAIL_BASE)
                    .defaultHeader("Authorization", "Bearer " + accessToken)
                    .build();

            GmailListResponse listResponse = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/me/messages")
                            .queryParam("maxResults", maxResults)
                            .queryParam("q", query)
                            .build())
                    .retrieve()
                    .body(GmailListResponse.class);

            if (listResponse == null || listResponse.getMessages() == null) {
                return List.of();
            }

            List<EmailMessage> emails = new ArrayList<>();
            for (GmailListResponse.GmailMessageRef ref : listResponse.getMessages()) {
                GmailMessageDetail detail = client.get()
                        .uri("/users/me/messages/{id}", ref.getId())
                        .retrieve()
                        .body(GmailMessageDetail.class);

                if (detail == null) {
                    continue;
                }

                emails.add(EmailMessage.builder()
                        .userId(user.getId())
                        .gmailMessageId(detail.getId())
                        .sender(GmailMessageParser.extractHeader(detail, "From"))
                        .subject(GmailMessageParser.extractHeader(detail, "Subject"))
                        .body(GmailMessageParser.extractBody(detail))
                        .receivedAt(GmailMessageParser.extractReceivedAt(detail))
                        .processed(false)
                        .build());
            }

            return emails;
        } catch (RestClientResponseException ex) {
            log.error("Gmail API error: {} {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new AiServiceException("Gmail API request failed: " + ex.getStatusCode(), ex);
        } catch (AiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to fetch Gmail messages", ex);
            throw new AiServiceException("Failed to fetch Gmail messages", ex);
        }
    }

    private String sanitizeFromEmail(String fromEmail) {
        if (!StringUtils.hasText(fromEmail)) {
            throw new IllegalArgumentException("Sender email address is required to sync inbox");
        }
        String trimmed = fromEmail.trim().toLowerCase();
        if (!trimmed.matches("^[a-z0-9._%+\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid sender email address");
        }
        return trimmed;
    }
}
