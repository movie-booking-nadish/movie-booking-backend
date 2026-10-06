package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;
import lk.ijse.cmjd.movie_booking_backend.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request, String currentUserEmail, boolean isAdmin);
    void deleteUser(Long id, String currentUserEmail);
}
