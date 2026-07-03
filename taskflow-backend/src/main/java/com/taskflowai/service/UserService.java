package com.taskflowai.service;

import com.taskflowai.dto.user.UserResponse;
import com.taskflowai.exception.ResourceNotFoundException;
import com.taskflowai.model.User;
import com.taskflowai.repository.UserRepository;
import com.taskflowai.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }
}
