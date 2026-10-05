package lk.ijse.cmjd.movie_booking_backend.dto;

import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long showId;
    private String movieTitle;
    private String theatreName;
    private LocalDate showDate;
    private LocalTime showTime;
    private List<String> seatNumbers;
    private Integer numberOfTickets;
    private BigDecimal totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;
}
