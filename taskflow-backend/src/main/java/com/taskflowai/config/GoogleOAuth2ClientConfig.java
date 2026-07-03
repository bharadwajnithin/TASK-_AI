package com.taskflowai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.oauth.google-enabled", havingValue = "true")
public class GoogleOAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(AppProperties appProperties) {
        String clientId = appProperties.getOauth().getGoogleClientId();
        String clientSecret = appProperties.getOauth().getGoogleClientSecret();

        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw new IllegalStateException(
                    "Google OAuth is enabled but GOOGLE_CLIENT_ID or GOOGLE_CLIENT_SECRET is missing. "
                            + "Set them in .env and start with .\\run.ps1");
        }

        String idPreview = clientId.length() > 12
                ? clientId.substring(0, 8) + "..." + clientId.substring(clientId.length() - 20)
                : clientId;
        log.info("Google OAuth configured with client ID: {}", idPreview);

        ClientRegistration googleLogin = ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("email", "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google Login")
                .build();

        ClientRegistration googleGmail = ClientRegistration.withRegistrationId("google-gmail")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(
                        "https://www.googleapis.com/auth/gmail.readonly",
                        "email",
                        "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google Gmail")
                .build();

        return new InMemoryClientRegistrationRepository(googleLogin, googleGmail);
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                return customizeGmailRequest(defaultResolver.resolve(request), request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                return customizeGmailRequest(
                        defaultResolver.resolve(request, clientRegistrationId), clientRegistrationId);
            }

            private OAuth2AuthorizationRequest customizeGmailRequest(
                    OAuth2AuthorizationRequest authorizationRequest, Object registrationIdHint) {
                if (authorizationRequest == null) {
                    return null;
                }
                String registrationId = registrationIdHint instanceof String id ? id : null;
                if (registrationId == null && registrationIdHint instanceof HttpServletRequest request) {
                    String uri = request.getRequestURI();
                    String base = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
                    if (uri.startsWith(base + "/")) {
                        registrationId = uri.substring(base.length() + 1);
                    }
                }
                if (!"google-gmail".equals(registrationId)) {
                    return authorizationRequest;
                }
                return OAuth2AuthorizationRequest.from(authorizationRequest)
                        .additionalParameters(params -> {
                            params.put("access_type", "offline");
                            params.put("prompt", "consent");
                        })
                        .build();
            }
        };
    }
}
