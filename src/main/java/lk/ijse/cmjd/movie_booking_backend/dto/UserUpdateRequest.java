package lk.ijse.cmjd.movie_booking_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lk.ijse.cmjd.movie_booking_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String password;
    private Role role;
}
