package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.MovieRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.MovieResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;

import java.util.List;

public interface MovieService {
    MovieResponse createMovie(MovieRequest request);
    MovieResponse updateMovie(Long id, MovieRequest request);
    void deleteMovie(Long id);
    MovieResponse getMovieById(Long id);
    List<MovieResponse> getAllMovies(String title, String language, String genre, MovieStatus status);
    lk.ijse.cmjd.movie_booking_backend.dto.PageResponse<MovieResponse> getMoviesPaginated(String title, String language, String genre, MovieStatus status, int page, int size);
}
