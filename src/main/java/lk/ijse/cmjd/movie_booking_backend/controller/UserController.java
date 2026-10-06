package lk.ijse.cmjd.movie_booking_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.cmjd.movie_booking_backend.dto.UserResponse;
import lk.ijse.cmjd.movie_booking_backend.dto.UserUpdateRequest;
import lk.ijse.cmjd.movie_booking_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Request: GET /api/users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUserOrAdmin(authentication, #id)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Request: GET /api/users/{}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUserOrAdmin(authentication, #id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication
    ) {
        log.info("Request: PUT /api/users/{}", id);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(userService.updateUser(id, request, authentication.getName(), isAdmin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Request: DELETE /api/users/{}", id);
        userService.deleteUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
