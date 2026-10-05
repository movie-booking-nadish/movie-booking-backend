package lk.ijse.cmjd.movie_booking_backend.mapper;

import lk.ijse.cmjd.movie_booking_backend.dto.BookingRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequest request, User user, Show show, BigDecimal totalAmount) {
        if (request == null) return null;
        return Booking.builder()
                .user(user)
                .show(show)
                .seatNumbers(new ArrayList<>(request.getSeatNumbers()))
                .numberOfTickets(request.getNumberOfTickets())
                .totalAmount(totalAmount)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.PENDING)
                .build();
    }

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) return null;
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .userFullName(booking.getUser() != null ? booking.getUser().getFullName() : null)
                .showId(booking.getShow() != null ? booking.getShow().getId() : null)
                .movieTitle(booking.getShow() != null && booking.getShow().getMovie() != null ? booking.getShow().getMovie().getTitle() : null)
                .theatreName(booking.getShow() != null && booking.getShow().getTheatre() != null ? booking.getShow().getTheatre().getName() : null)
                .showDate(booking.getShow() != null ? booking.getShow().getShowDate() : null)
                .showTime(booking.getShow() != null ? booking.getShow().getShowTime() : null)
                .seatNumbers(booking.getSeatNumbers())
                .numberOfTickets(booking.getNumberOfTickets())
                .totalAmount(booking.getTotalAmount())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .build();
    }
}
