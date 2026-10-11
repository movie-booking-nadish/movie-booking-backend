package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;
import lk.ijse.cmjd.movie_booking_backend.dto.UserUpdateRequest;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.exception.BadRequestException;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.UserMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.BookingRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.UserRepository;
import lk.ijse.cmjd.movie_booking_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request, String currentUserEmail, boolean isAdmin) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.info("Updated password for user id: {}", id);
        }

        if (isAdmin && request.getRole() != null) {
            user.setRole(request.getRole());
            log.info("Updated role to {} for user id: {}", request.getRole(), id);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String currentUserEmail) {
        log.info("Attempting to delete user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getEmail().equalsIgnoreCase(currentUserEmail)) {
            log.warn("Admin attempted to delete their own account: {}", currentUserEmail);
            throw new BadRequestException("An admin cannot delete their own account");
        }

        if (bookingRepository.existsByUser_Id(id)) {
            log.warn("Cannot delete user with id: {} as they have associated bookings", id);
            throw new DuplicateResourceException("Cannot delete user with id " + id + " because they have existing bookings");
        }

        userRepository.delete(user);
        log.info("Successfully deleted user with id: {}", id);
    }
}
