package com.taskflowai.security;

import com.taskflowai.config.AppProperties;
import com.taskflowai.exception.ResourceNotFoundException;
import com.taskflowai.model.Role;
import com.taskflowai.model.User;
import com.taskflowai.repository.UserRepository;
import com.taskflowai.service.GmailTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.oauth.google-enabled", havingValue = "true")
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AppProperties appProperties;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GmailTokenService gmailTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = String.valueOf(attributes.get("email")).toLowerCase().trim();

        if ("google-gmail".equals(oauthToken.getAuthorizedClientRegistrationId())) {
            handleGmailConnect(request, response, oauthToken, email);
            return;
        }

        handleLogin(request, response, oauth2User, attributes, email);
    }

    private void handleGmailConnect(
            HttpServletRequest request,
            HttpServletResponse response,
            OAuth2AuthenticationToken oauthToken,
            String email) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No TaskFlow account found for " + email + ". Register first with the same email."));

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            Long expiresIn = null;
            if (accessToken.getExpiresAt() != null) {
                expiresIn = ChronoUnit.SECONDS.between(Instant.now(), accessToken.getExpiresAt());
            }

            gmailTokenService.saveTokens(
                    user,
                    accessToken.getTokenValue(),
                    refreshToken != null ? refreshToken.getTokenValue() : user.getGmailRefreshToken(),
                    expiresIn);
        }

        String redirectUrl = appProperties.getFrontendUrl() + "/gmail?connected=true";
        log.info("Gmail connected for user: {}", email);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void handleLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            OAuth2User oauth2User,
            Map<String, Object> attributes,
            String email) throws IOException {

        String googleId = String.valueOf(attributes.get("sub"));
        String fullName = attributes.get("name") != null
                ? String.valueOf(attributes.get("name"))
                : email;
        String picture = attributes.get("picture") != null
                ? String.valueOf(attributes.get("picture"))
                : null;

        User user = findOrCreateOAuthUser(googleId, email, fullName, picture);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(appProperties.getFrontendUrl() + "/oauth/callback")
                .queryParam("token", token)
                .queryParam("expiresIn", jwtService.getExpirationMs())
                .build()
                .toUriString();

        log.info("Google OAuth success for user: {}", email);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private User findOrCreateOAuthUser(String googleId, String email, String fullName, String picture) {
        Optional<User> byGoogleId = userRepository.findByGoogleId(googleId);
        if (byGoogleId.isPresent()) {
            return byGoogleId.get();
        }

        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User existing = byEmail.get();
            existing.setGoogleId(googleId);
            existing.setOauthUser(true);
            if (picture != null) {
                existing.setProfilePictureUrl(picture);
            }
            return userRepository.save(existing);
        }

        User newUser = User.builder()
                .fullName(fullName)
                .email(email)
                .googleId(googleId)
                .profilePictureUrl(picture)
                .role(Role.USER)
                .oauthUser(true)
                .createdAt(Instant.now())
                .build();

        return userRepository.save(newUser);
    }
}
