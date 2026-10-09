package lk.ijse.cmjd.movie_booking_backend.controller;

import jakarta.validation.Valid;
import lk.ijse.cmjd.movie_booking_backend.dto.PaymentRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.PaymentResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;
import lk.ijse.cmjd.movie_booking_backend.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication
    ) {
        log.info("Request: POST /api/payments for booking id {} by user {}", request.getBookingId(), authentication.getName());
        PaymentResponse response = paymentService.createPayment(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "true") Boolean success,
            Authentication authentication
    ) {
        log.info("Request: POST /api/payments/{}/process (success={}) by user {}", id, success, authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        PaymentResponse response = paymentService.processPayment(id, success, authentication.getName(), isAdmin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Request: GET /api/payments/{} by user {}", id, authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(paymentService.getPaymentById(id, authentication.getName(), isAdmin));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, PaymentStatus>> getPaymentStatus(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Request: GET /api/payments/{}/status by user {}", id, authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        PaymentStatus status = paymentService.getPaymentStatus(id, authentication.getName(), isAdmin);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {
        log.info("Request: GET /api/payments/booking/{} by user {}", bookingId, authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(paymentService.getPaymentByBookingId(bookingId, authentication.getName(), isAdmin));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        log.info("Admin request: GET /api/payments");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
