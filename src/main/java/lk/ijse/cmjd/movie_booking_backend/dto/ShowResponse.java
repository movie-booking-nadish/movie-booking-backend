package lk.ijse.cmjd.movie_booking_backend.dto;

import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theatreId;
    private String theatreName;
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal ticketPrice;
    private ShowStatus status;
}
