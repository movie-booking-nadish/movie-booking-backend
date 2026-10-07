package lk.ijse.cmjd.movie_booking_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.cmjd.movie_booking_backend.dto.ShowRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.ShowResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import lk.ijse.cmjd.movie_booking_backend.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        log.info("Admin request: POST /api/shows - movie: {}, theatre: {}, date: {}",
                request.getMovieId(), request.getTheatreId(), request.getShowDate());
        ShowResponse response = showService.createShow(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> updateShow(@PathVariable Long id, @Valid @RequestBody ShowRequest request) {
        log.info("Admin request: PUT /api/shows/{}", id);
        ShowResponse response = showService.updateShow(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        log.info("Admin request: DELETE /api/shows/{}", id);
        showService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long theatreId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate,
            @RequestParam(required = false) ShowStatus status
    ) {
        log.info("Request: GET /api/shows (movieId={}, theatreId={}, showDate={}, status={})",
                movieId, theatreId, showDate, status);
        return ResponseEntity.ok(showService.getAllShows(movieId, theatreId, showDate, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long id) {
        log.info("Request: GET /api/shows/{}", id);
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponse>> getShowsByMovieId(@PathVariable Long movieId) {
        log.info("Request: GET /api/shows/movie/{}", movieId);
        return ResponseEntity.ok(showService.getShowsByMovieId(movieId));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<lk.ijse.cmjd.movie_booking_backend.dto.SeatAvailabilityResponse> getSeatAvailability(@PathVariable Long id) {
        log.info("Request: GET /api/shows/{}/seats", id);
        return ResponseEntity.ok(showService.getSeatAvailability(id));
    }
}
