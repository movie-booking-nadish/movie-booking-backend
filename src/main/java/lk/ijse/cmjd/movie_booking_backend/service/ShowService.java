package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.SeatAvailabilityResponse;
import lk.ijse.cmjd.movie_booking_backend.dto.ShowRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.ShowResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;

import java.time.LocalDate;
import java.util.List;

public interface ShowService {
    ShowResponse createShow(ShowRequest request);
    ShowResponse updateShow(Long id, ShowRequest request);
    void deleteShow(Long id);
    ShowResponse getShowById(Long id);
    List<ShowResponse> getAllShows(Long movieId, Long theatreId, LocalDate showDate, ShowStatus status);
    List<ShowResponse> getShowsByMovieId(Long movieId);
    SeatAvailabilityResponse getSeatAvailability(Long showId);
}
