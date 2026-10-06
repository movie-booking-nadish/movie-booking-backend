package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.SignUpRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.Role;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.mapper.UserMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.UserRepository;
import lk.ijse.cmjd.movie_booking_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        log.info("Processing signup request for email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Signup failed: Email {} is already registered", request.getEmail());
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, encodedPassword, Role.CUSTOMER);
        User savedUser = userRepository.save(user);
        log.info("Successfully registered customer with id: {} and email: {}", savedUser.getId(), savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }
}
