package com.taskflowai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final AppProperties appProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> rawOrigins = appProperties.getCors().getAllowedOrigins();
        List<String> parsedOrigins = new ArrayList<>();

        if (rawOrigins != null && !rawOrigins.isEmpty()) {
            for (String raw : rawOrigins) {
                if (raw != null) {
                    for (String part : raw.split(",")) {
                        String trimmed = part.trim();
                        if (!trimmed.isEmpty()) {
                            parsedOrigins.add(trimmed);
                        }
                    }
                }
            }
        }

        // Include Vercel domain patterns & localhost defaults
        if (!parsedOrigins.contains("https://*.vercel.app")) {
            parsedOrigins.add("https://*.vercel.app");
        }
        if (!parsedOrigins.contains("http://localhost:5173")) {
            parsedOrigins.add("http://localhost:5173");
        }
        if (!parsedOrigins.contains("http://localhost:3000")) {
            parsedOrigins.add("http://localhost:3000");
        }

        configuration.setAllowedOriginPatterns(parsedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
