package lk.ijse.cmjd.movie_booking_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheatreRequest {

    @NotBlank(message = "Theatre name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Status is required")
    private TheatreStatus status;
}
