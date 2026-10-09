package lk.ijse.cmjd.movie_booking_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import lk.ijse.cmjd.movie_booking_backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        log.info("Request: POST /api/bookings by user {}", authentication.getName());
        BookingResponse response = bookingService.createBooking(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Request: GET /api/bookings/{} by user {}", id, authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(bookingService.getBookingById(id, authentication.getName(), isAdmin));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Request: GET /api/bookings/my-bookings by user {} (page={}, size={})", authentication.getName(), page, size);
        if (page != null) {
            return ResponseEntity.ok(bookingService.getMyBookingsPaginated(authentication.getName(), page, size));
        }
        return ResponseEntity.ok(bookingService.getMyBookings(authentication.getName()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Request: PUT /api/bookings/{}/cancel by user {}", id, authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(bookingService.cancelBooking(id, authentication.getName(), isAdmin));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllBookings(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("Admin request: GET /api/bookings (page={}, size={})", page, size);
        if (page != null) {
            return ResponseEntity.ok(bookingService.getAllBookingsPaginated(page, size));
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam(required = false) BookingStatus status,
            @RequestBody(required = false) Map<String, String> body
    ) {
        BookingStatus targetStatus = status;
        if (targetStatus == null && body != null && body.containsKey("status")) {
            targetStatus = BookingStatus.valueOf(body.get("status").toUpperCase());
        }
        if (targetStatus == null) {
            throw new IllegalArgumentException("Status is required to update booking status");
        }
        log.info("Admin request: PUT /api/bookings/{}/status -> {}", id, targetStatus);
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, targetStatus));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
