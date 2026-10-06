package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.SignUpRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;

public interface AuthService {
    UserResponse signUp(SignUpRequest request);
}
