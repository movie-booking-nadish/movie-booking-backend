package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.PaymentRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.PaymentResponse;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request, String currentUserEmail);
    PaymentResponse processPayment(Long paymentId, Boolean success, String currentUserEmail, boolean isAdmin);
    PaymentResponse getPaymentById(Long id, String currentUserEmail, boolean isAdmin);
    PaymentStatus getPaymentStatus(Long id, String currentUserEmail, boolean isAdmin);
    PaymentResponse getPaymentByBookingId(Long bookingId, String currentUserEmail, boolean isAdmin);
    List<PaymentResponse> getAllPayments();
}
