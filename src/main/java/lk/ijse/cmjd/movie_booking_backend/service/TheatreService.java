package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.TheatreRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.TheatreResponse;

import java.util.List;

public interface TheatreService {
    TheatreResponse createTheatre(TheatreRequest request);
    TheatreResponse updateTheatre(Long id, TheatreRequest request);
    void deleteTheatre(Long id);
    TheatreResponse getTheatreById(Long id);
    List<TheatreResponse> getAllTheatres();
}
