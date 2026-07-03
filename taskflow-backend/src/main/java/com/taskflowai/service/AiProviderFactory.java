package com.taskflowai.service;

import com.taskflowai.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiProviderFactory {

    private final AppProperties appProperties;
    private final GeminiAiProvider geminiAiProvider;
    private final OpenAiProvider openAiProvider;

    public AiProvider getProvider() {
        String provider = appProperties.getAi().getProvider();
        if (provider == null) {
            provider = "gemini";
        }

        log.info("Using AI provider: {}", provider);

        return switch (provider.toLowerCase()) {
            case "openai" -> openAiProvider;
            case "gemini" -> geminiAiProvider;
            default -> {
                log.warn("Unknown AI provider: {}, falling back to Gemini", provider);
                yield geminiAiProvider;
            }
        };
    }
}
