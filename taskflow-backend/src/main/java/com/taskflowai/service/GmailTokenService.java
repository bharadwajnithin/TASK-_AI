package com.taskflowai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflowai.config.AppProperties;
import com.taskflowai.exception.AiServiceException;
import com.taskflowai.model.User;
import com.taskflowai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailTokenService {

    private final UserRepository userRepository;
    private final AppProperties appProperties;
    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    public String getValidAccessToken(User user) {
        if (!user.isGmailConnected() || !StringUtils.hasText(user.getGmailRefreshToken())) {
            throw new AiServiceException("Gmail is not connected. Connect your Gmail account first.");
        }

        if (StringUtils.hasText(user.getGmailAccessToken())
                && user.getGmailTokenExpiry() != null
                && user.getGmailTokenExpiry().isAfter(Instant.now().plusSeconds(60))) {
            return user.getGmailAccessToken();
        }

        return refreshAccessToken(user);
    }

    public void saveTokens(User user, String accessToken, String refreshToken, Long expiresInSeconds) {
        user.setGmailConnected(true);
        user.setGmailAccessToken(accessToken);
        if (StringUtils.hasText(refreshToken)) {
            user.setGmailRefreshToken(refreshToken);
        }
        if (expiresInSeconds != null) {
            user.setGmailTokenExpiry(Instant.now().plusSeconds(expiresInSeconds));
        }
        userRepository.save(user);
    }

    public void disconnect(User user) {
        user.setGmailConnected(false);
        user.setGmailAccessToken(null);
        user.setGmailRefreshToken(null);
        user.setGmailTokenExpiry(null);
        user.setGmailSyncFromEmail(null);
        user.setGmailSyncFromEmails(new java.util.ArrayList<>());
        userRepository.save(user);
    }

    private String refreshAccessToken(User user) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", appProperties.getOauth().getGoogleClientId());
        form.add("client_secret", appProperties.getOauth().getGoogleClientSecret());
        form.add("refresh_token", user.getGmailRefreshToken());
        form.add("grant_type", "refresh_token");

        try {
            String responseBody = RestClient.builder().build()
                    .post()
                    .uri("https://oauth2.googleapis.com/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);

            JsonNode json = objectMapper.readTree(responseBody);
            String accessToken = json.path("access_token").asText();
            long expiresIn = json.path("expires_in").asLong(3600);

            user.setGmailAccessToken(accessToken);
            user.setGmailTokenExpiry(Instant.now().plusSeconds(expiresIn));
            userRepository.save(user);

            return accessToken;
        } catch (Exception ex) {
            log.error("Failed to refresh Gmail access token for user {}", user.getEmail(), ex);
            throw new AiServiceException("Failed to refresh Gmail access token. Reconnect Gmail.");
        }
    }
}
