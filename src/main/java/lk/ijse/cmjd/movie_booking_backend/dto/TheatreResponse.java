package lk.ijse.cmjd.movie_booking_backend.dto;

import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheatreResponse {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private TheatreStatus status;
}
