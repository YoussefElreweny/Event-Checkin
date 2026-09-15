package com.youssef.eventcheckin.user;

import com.youssef.eventcheckin.user.dto.CreateUserRequest;
import com.youssef.eventcheckin.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(request.password());
        user.setFullName(request.fullName());
        user.setRole(request.role());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getById(UUID id) {
        return userRepository.findById(id).map(UserResponse::from);
    }
}