package com.taskflowai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.oauth.google-enabled", havingValue = "true")
public class OAuth2ConditionalConfig {
}
