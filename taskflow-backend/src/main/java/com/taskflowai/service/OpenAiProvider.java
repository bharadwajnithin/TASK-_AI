package com.taskflowai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflowai.config.AppProperties;
import com.taskflowai.dto.ai.AiExtractionPayload;
import com.taskflowai.exception.AiServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiProvider implements AiProvider {

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant for a project management SaaS called OMNITASK.
            Extract actionable tasks from client communications (emails, chats, notes).

            Rules:
            - Identify ALL distinct actionable tasks.
            - Infer priority: LOW, MEDIUM, or HIGH (urgent/deadline soon = HIGH).
            - Extract deadlines; use dueDate as ISO yyyy-MM-dd when possible, else null.
            - Put human-readable deadline in dueDateText (e.g. "Friday", "next Monday").
            - Detect clientName and projectName when mentioned.
            - Return ONLY valid JSON matching this schema:
            {
              "clientName": "string or null",
              "projectName": "string or null",
              "tasks": [
                {
                  "title": "short action title",
                  "description": "clear task description",
                  "priority": "LOW|MEDIUM|HIGH",
                  "dueDate": "yyyy-MM-dd or null",
                  "dueDateText": "human readable or null"
                }
              ]
            }
            """;

    private final AppProperties appProperties;
    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public AiExtractionPayload extractTasks(String content) {
        String apiKey = appProperties.getAi().getApiKey();
        if (!StringUtils.hasText(apiKey)) {
            throw new AiServiceException("AI API key is not configured. Set AI_API_KEY.");
        }

        String model = appProperties.getAi().getModel();
        String baseUrl = appProperties.getAi().getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            baseUrl = "https://api.openai.com/v1";
        }

        OpenAiRequest request = OpenAiRequest.builder()
                .model(model)
                .messages(List.of(
                        OpenAiRequest.Message.builder()
                                .role("system")
                                .content(SYSTEM_PROMPT)
                                .build(),
                        OpenAiRequest.Message.builder()
                                .role("user")
                                .content(content)
                                .build()
                ))
                .responseFormat(Map.of("type", "json_object"))
                .temperature(0.2)
                .build();

        try {
            RestClient client = RestClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .build();

            OpenAiResponse response = client.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OpenAiResponse.class);

            String json = extractText(response);
            log.debug("OpenAI extraction response: {}", json);

            return objectMapper.readValue(json, AiExtractionPayload.class);
        } catch (RestClientResponseException ex) {
            log.error("OpenAI API error: {} {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new AiServiceException("OpenAI API request failed: " + ex.getStatusCode(), ex);
        } catch (AiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to call OpenAI", ex);
            throw new AiServiceException("Failed to extract tasks using OpenAI", ex);
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

    private String extractText(OpenAiResponse response) {
        if (response == null
                || response.getChoices() == null
                || response.getChoices().isEmpty()
                || response.getChoices().get(0).getMessage() == null
                || response.getChoices().get(0).getMessage().getContent() == null) {
            throw new AiServiceException("Empty response from OpenAI");
        }
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OpenAiRequest {
        private String model;
        private List<Message> messages;
        private Map<String, Object> responseFormat;
        private Double temperature;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OpenAiResponse {
        private List<Choice> choices;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Choice {
            private Message message;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Message {
            private String content;
        }
    }
}
