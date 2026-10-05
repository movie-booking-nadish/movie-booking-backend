package lk.ijse.cmjd.movie_booking_backend.mapper;

import lk.ijse.cmjd.movie_booking_backend.dto.MovieRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.MovieResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequest request) {
        if (request == null) return null;
        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .language(request.getLanguage())
                .genre(request.getGenre())
                .releaseDate(request.getReleaseDate())
                .status(request.getStatus())
                .posterUrl(request.getPosterUrl())
                .build();
    }

    public MovieResponse toResponse(Movie movie) {
        if (movie == null) return null;
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .language(movie.getLanguage())
                .genre(movie.getGenre())
                .releaseDate(movie.getReleaseDate())
                .status(movie.getStatus())
                .posterUrl(movie.getPosterUrl())
                .build();
    }

    public void updateEntity(Movie movie, MovieRequest request) {
        if (movie == null || request == null) return;
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setLanguage(request.getLanguage());
        movie.setGenre(request.getGenre());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setStatus(request.getStatus());
        movie.setPosterUrl(request.getPosterUrl());
    }
}
