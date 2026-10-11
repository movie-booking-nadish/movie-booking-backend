package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.PaymentRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.PaymentResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import lk.ijse.cmjd.movie_booking_backend.entity.Payment;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentMethod;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.BusinessRuleException;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.mapper.PaymentMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.BookingRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.PaymentRepository;
import lk.ijse.cmjd.movie_booking_backend.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User testUser;
    private Booking testBooking;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("customer@moviebooking.com")
                .fullName("Test Customer")
                .build();

        testBooking = Booking.builder()
                .id(5L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(1500.00))
                .status(BookingStatus.PENDING)
                .build();

        testPayment = Payment.builder()
                .id(10L)
                .booking(testBooking)
                .amount(BigDecimal.valueOf(1500.00))
                .paymentMethod(PaymentMethod.CARD)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void testCreatePayment_Success() {
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(5L)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBooking_Id(5L)).thenReturn(Optional.empty());
        when(paymentMapper.toEntity(eq(request), eq(testBooking), any(BigDecimal.class), eq(PaymentStatus.PENDING)))
                .thenReturn(testPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentMapper.toResponse(testPayment)).thenReturn(PaymentResponse.builder().id(10L).status(PaymentStatus.PENDING).build());

        PaymentResponse response = paymentService.createPayment(request, "customer@moviebooking.com");

        assertNotNull(response);
        assertEquals(PaymentStatus.PENDING, response.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_FailsWhenBookingCancelled() {
        testBooking.setStatus(BookingStatus.CANCELLED);
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(5L)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(testBooking));

        assertThrows(BusinessRuleException.class, () ->
                paymentService.createPayment(request, "customer@moviebooking.com")
        );
    }

    @Test
    void testCreatePayment_FailsWhenPaymentAlreadyExists() {
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(5L)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBooking_Id(5L)).thenReturn(Optional.of(testPayment));

        assertThrows(DuplicateResourceException.class, () ->
                paymentService.createPayment(request, "customer@moviebooking.com")
        );
    }

    @Test
    void testCreatePayment_FailsWhenWrongUser() {
        PaymentRequest request = PaymentRequest.builder()
                .bookingId(5L)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(testBooking));

        assertThrows(AccessDeniedException.class, () ->
                paymentService.createPayment(request, "other@example.com")
        );
    }

    @Test
    void testProcessPayment_SuccessConfirmsBooking() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentMapper.toResponse(testPayment)).thenReturn(PaymentResponse.builder().id(10L).status(PaymentStatus.COMPLETED).build());

        PaymentResponse response = paymentService.processPayment(10L, true, "customer@moviebooking.com", false);

        assertNotNull(response);
        assertEquals(PaymentStatus.COMPLETED, testPayment.getStatus());
        assertEquals(BookingStatus.CONFIRMED, testBooking.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testProcessPayment_FailsWhenAlreadyCompleted() {
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(testPayment));

        assertThrows(BusinessRuleException.class, () ->
                paymentService.processPayment(10L, true, "customer@moviebooking.com", false)
        );
    }
}
