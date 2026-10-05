package lk.ijse.cmjd.movie_booking_backend.mapper;

import lk.ijse.cmjd.movie_booking_backend.dto.SignUpRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toEntity(SignUpRequest request, String encodedPassword, Role role) {
        if (request == null) return null;
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phone(request.getPhone())
                .role(role != null ? role : Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
