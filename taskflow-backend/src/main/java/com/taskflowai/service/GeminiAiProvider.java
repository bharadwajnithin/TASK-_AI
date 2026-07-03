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

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAiProvider implements AiProvider {

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
            baseUrl = "https://generativelanguage.googleapis.com/v1beta";
        }

        GeminiRequest request = GeminiRequest.builder()
                .systemInstruction(GeminiRequest.SystemInstruction.builder()
                        .parts(List.of(GeminiRequest.Part.builder()
                                .text(SYSTEM_PROMPT)
                                .build()))
                        .build())
                .contents(List.of(GeminiRequest.Content.builder()
                        .role("user")
                        .parts(List.of(GeminiRequest.Part.builder()
                                .text(content)
                                .build()))
                        .build()))
                .generationConfig(GeminiRequest.GenerationConfig.builder()
                        .responseMimeType("application/json")
                        .temperature(0.2)
                        .build())
                .build();

        try {
            RestClient client = restClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader("x-goog-api-key", apiKey)
                    .build();

            GeminiResponse response = client.post()
                    .uri("/models/{model}:generateContent", model)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            String json = extractText(response);
            log.debug("Gemini extraction response: {}", json);

            return objectMapper.readValue(json, AiExtractionPayload.class);
        } catch (RestClientResponseException ex) {
            log.error("Gemini API error: {} {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new AiServiceException("Gemini API request failed: " + ex.getStatusCode(), ex);
        } catch (AiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to call Gemini", ex);
            throw new AiServiceException("Failed to extract tasks using Gemini AI", ex);
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    private String extractText(GeminiResponse response) {
        if (response == null
                || response.getCandidates() == null
                || response.getCandidates().isEmpty()
                || response.getCandidates().get(0).getContent() == null
                || response.getCandidates().get(0).getContent().getParts() == null
                || response.getCandidates().get(0).getContent().getParts().isEmpty()) {
            throw new AiServiceException("Empty response from Gemini");
        }
        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeminiRequest {
        private SystemInstruction systemInstruction;
        private List<Content> contents;
        private GenerationConfig generationConfig;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class SystemInstruction {
            private List<Part> parts;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Content {
            private String role;
            private List<Part> parts;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Part {
            private String text;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class GenerationConfig {
            private String responseMimeType;
            private double temperature;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeminiResponse {
        private List<Candidate> candidates;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Candidate {
            private Content content;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Content {
            private List<Part> parts;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Part {
            private String text;
        }
    }
}
