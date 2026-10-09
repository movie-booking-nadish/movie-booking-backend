package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.PaymentRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.PaymentResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import lk.ijse.cmjd.movie_booking_backend.entity.Payment;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.BusinessRuleException;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.PaymentMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.BookingRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.PaymentRepository;
import lk.ijse.cmjd.movie_booking_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, String currentUserEmail) {
        log.info("Creating payment for booking id {} by user {}", request.getBookingId(), currentUserEmail);
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        if (!booking.getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You do not have permission to pay for this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled bookings cannot be paid for");
        }

        if (paymentRepository.findByBooking_Id(booking.getId()).isPresent()) {
            throw new DuplicateResourceException("A payment already exists for booking id: " + booking.getId());
        }

        Payment payment = paymentMapper.toEntity(request, booking, booking.getTotalAmount(), PaymentStatus.PENDING);
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(Long paymentId, Boolean success, String currentUserEmail, boolean isAdmin) {
        log.info("Processing payment id {} (success={}) by user {}", paymentId, success, currentUserEmail);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!isAdmin && !payment.getBooking().getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You do not have permission to process this payment");
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessRuleException("A completed payment cannot be processed again");
        }

        boolean isSuccessful = (success == null || success);
        if (isSuccessful) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.getBooking().setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(payment.getBooking());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id, String currentUserEmail, boolean isAdmin) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (!isAdmin && !payment.getBooking().getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You do not have permission to view this payment");
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatus getPaymentStatus(Long id, String currentUserEmail, boolean isAdmin) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (!isAdmin && !payment.getBooking().getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You do not have permission to view this payment status");
        }

        return payment.getStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId, String currentUserEmail, boolean isAdmin) {
        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking id: " + bookingId));

        if (!isAdmin && !payment.getBooking().getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You do not have permission to view this payment");
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
