package lk.ijse.cmjd.movie_booking_backend.mapper;

import lk.ijse.cmjd.movie_booking_backend.dto.PaymentRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.PaymentResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import lk.ijse.cmjd.movie_booking_backend.entity.Payment;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request, Booking booking, BigDecimal amount, PaymentStatus status) {
        if (request == null) return null;
        return Payment.builder()
                .booking(booking)
                .amount(amount)
                .paymentDate(LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .status(status != null ? status : PaymentStatus.PENDING)
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking() != null ? payment.getBooking().getId() : null)
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .build();
    }
}
