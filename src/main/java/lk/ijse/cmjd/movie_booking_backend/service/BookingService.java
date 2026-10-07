package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.BookingRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String currentUserEmail);
    BookingResponse getBookingById(Long id, String currentUserEmail, boolean isAdmin);
    List<BookingResponse> getMyBookings(String currentUserEmail);
    BookingResponse cancelBooking(Long id, String currentUserEmail, boolean isAdmin);
    List<BookingResponse> getAllBookings();
    BookingResponse updateBookingStatus(Long id, BookingStatus status);
}
