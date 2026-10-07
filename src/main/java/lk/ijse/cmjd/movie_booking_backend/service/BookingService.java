package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.BookingRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String currentUserEmail);
}
