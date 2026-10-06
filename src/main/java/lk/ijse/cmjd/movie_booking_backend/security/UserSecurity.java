package lk.ijse.cmjd.movie_booking_backend.security;

import lk.ijse.cmjd.movie_booking_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isCurrentUserOrAdmin(Authentication authentication, Long userId) {
        if (authentication == null || userId == null) {
            return false;
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }
        String currentEmail = authentication.getName();
        return userRepository.findById(userId)
                .map(user -> user.getEmail().equalsIgnoreCase(currentEmail))
                .orElse(false);
    }
}
