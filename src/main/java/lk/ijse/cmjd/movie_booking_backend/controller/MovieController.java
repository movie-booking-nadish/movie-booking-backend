package lk.ijse.cmjd.movie_booking_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.cmjd.movie_booking_backend.dto.MovieRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.MovieResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import lk.ijse.cmjd.movie_booking_backend.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        log.info("Admin request: POST /api/movies - title: {}", request.getTitle());
        MovieResponse response = movieService.createMovie(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        log.info("Admin request: PUT /api/movies/{} - title: {}", id, request.getTitle());
        MovieResponse response = movieService.updateMovie(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("Admin request: DELETE /api/movies/{}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Request: GET /api/movies (title={}, language={}, genre={}, status={}, page={}, size={})",
                title, language, genre, status, page, size);
        if (page != null) {
            return ResponseEntity.ok(movieService.getMoviesPaginated(title, language, genre, status, page, size));
        }
        return ResponseEntity.ok(movieService.getAllMovies(title, language, genre, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        log.info("Request: GET /api/movies/{}", id);
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }
}
