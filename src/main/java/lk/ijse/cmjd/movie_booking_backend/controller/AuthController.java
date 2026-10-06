package lk.ijse.cmjd.movie_booking_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.cmjd.movie_booking_backend.dto.SignUpRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;
import lk.ijse.cmjd.movie_booking_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("Received signup request for email: {}", request.getEmail());
        UserResponse response = authService.signUp(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
