package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.MovieRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.MovieResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.MovieMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.MovieRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        log.info("Creating movie with title: {}", request.getTitle());
        Movie movie = movieMapper.toEntity(request);
        Movie savedMovie = movieRepository.save(movie);
        log.info("Successfully created movie with id: {}", savedMovie.getId());
        return movieMapper.toResponse(savedMovie);
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        log.info("Updating movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movieMapper.updateEntity(movie, request);
        Movie updatedMovie = movieRepository.save(movie);
        log.info("Successfully updated movie with id: {}", updatedMovie.getId());
        return movieMapper.toResponse(updatedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        log.info("Attempting to delete movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        if (showRepository.existsByMovie_Id(id)) {
            log.warn("Cannot delete movie with id: {} as it has associated shows", id);
            throw new DuplicateResourceException("Cannot delete movie with id " + id + " because it has scheduled shows");
        }

        movieRepository.delete(movie);
        log.info("Successfully deleted movie with id: {}", id);
    }

    @Override
    public MovieResponse getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return movieMapper.toResponse(movie);
    }

    @Override
    public List<MovieResponse> getAllMovies(String title, String language, String genre, MovieStatus status) {
        log.info("Fetching movies with filters - title: {}, language: {}, genre: {}, status: {}",
                title, language, genre, status);
        List<Movie> movies;
        if (title == null && language == null && genre == null && status == null) {
            movies = movieRepository.findAll();
        } else {
            movies = movieRepository.searchMovies(title, language, genre, status);
        }

        return movies.stream()
                .map(movieMapper::toResponse)
                .collect(Collectors.toList());
    }
}
