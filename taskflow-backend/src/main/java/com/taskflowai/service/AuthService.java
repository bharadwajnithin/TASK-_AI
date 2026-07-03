package com.taskflowai.service;

import com.taskflowai.dto.auth.AuthResponse;
import com.taskflowai.dto.auth.LoginRequest;
import com.taskflowai.dto.auth.RegisterRequest;
import com.taskflowai.exception.DuplicateResourceException;
import com.taskflowai.exception.UnauthorizedException;
import com.taskflowai.model.Role;
import com.taskflowai.model.User;
import com.taskflowai.repository.UserRepository;
import com.taskflowai.security.CustomUserDetails;
import com.taskflowai.security.JwtService;
import com.taskflowai.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered: " + email);
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .oauthUser(false)
                .createdAt(Instant.now())
                .build();

        User saved = userRepository.save(user);
        log.info("User registered: {}", saved.getEmail());

        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.isOauthUser() && user.getPassword() == null) {
            throw new UnauthorizedException("Please sign in with Google for this account");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        log.info("User logged in: {}", email);
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .user(UserMapper.toResponse(user))
                .build();
    }
}
