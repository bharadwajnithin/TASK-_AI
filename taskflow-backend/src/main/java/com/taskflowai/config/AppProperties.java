package com.taskflowai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private OAuth oauth = new OAuth();
    private Ai ai = new Ai();
    private String frontendUrl = "http://localhost:5173";

    @Getter
    @Setter
    public static class Ai {
        private String provider = "gemini";
        private String apiKey;
        private String model = "gemini-2.5-flash";
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class OAuth {
        private boolean googleEnabled = false;
        private String googleClientId;
        private String googleClientSecret;
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMs = 86400000L;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:5173");
    }
}
