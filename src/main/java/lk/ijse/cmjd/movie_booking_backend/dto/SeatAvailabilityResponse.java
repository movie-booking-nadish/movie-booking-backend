package lk.ijse.cmjd.movie_booking_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAvailabilityResponse {
    private Long showId;
    private Integer capacity;
    private Integer seatsPerRow;
    private List<String> bookedSeats;
}
