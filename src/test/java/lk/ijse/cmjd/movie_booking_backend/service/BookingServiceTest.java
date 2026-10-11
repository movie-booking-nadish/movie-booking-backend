package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.BookingRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.entity.Payment;
import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.Role;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.BadRequestException;
import lk.ijse.cmjd.movie_booking_backend.exception.BusinessRuleException;
import lk.ijse.cmjd.movie_booking_backend.mapper.BookingMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.BookingRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.PaymentRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.UserRepository;
import lk.ijse.cmjd.movie_booking_backend.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User testUser;
    private Show testShow;
    private Theatre testTheatre;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("customer@moviebooking.com")
                .fullName("Test Customer")
                .role(Role.CUSTOMER)
                .build();

        testTheatre = Theatre.builder()
                .id(1L)
                .name("Grand Cinema")
                .capacity(50)
                .status(TheatreStatus.ACTIVE)
                .build();

        testShow = Show.builder()
                .id(1L)
                .showDate(LocalDate.now().plusDays(2))
                .showTime(LocalTime.of(18, 0))
                .ticketPrice(BigDecimal.valueOf(1000.00))
                .status(ShowStatus.SCHEDULED)
                .theatre(testTheatre)
                .build();
    }

    @Test
    void testCreateBooking_Success() {
        BookingRequest request = BookingRequest.builder()
                .showId(1L)
                .numberOfTickets(2)
                .seatNumbers(List.of("A1", "A2"))
                .build();

        when(userRepository.findByEmail("customer@moviebooking.com")).thenReturn(Optional.of(testUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
        when(bookingRepository.findBookedSeatsByShowId(1L)).thenReturn(new ArrayList<>());

        Booking entity = Booking.builder()
                .id(10L)
                .user(testUser)
                .show(testShow)
                .seatNumbers(List.of("A1", "A2"))
                .numberOfTickets(2)
                .totalAmount(BigDecimal.valueOf(2000.00))
                .status(BookingStatus.PENDING)
                .build();

        when(bookingMapper.toEntity(eq(request), eq(testUser), eq(testShow), any(BigDecimal.class))).thenReturn(entity);
        when(bookingRepository.save(any(Booking.class))).thenReturn(entity);
        when(bookingMapper.toResponse(entity)).thenReturn(BookingResponse.builder().id(10L).build());

        BookingResponse response = bookingService.createBooking(request, "customer@moviebooking.com");

        assertNotNull(response);
        assertEquals(10L, response.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_FailsWhenShowNotScheduled() {
        testShow.setStatus(ShowStatus.COMPLETED);

        BookingRequest request = BookingRequest.builder()
                .showId(1L)
                .numberOfTickets(1)
                .seatNumbers(List.of("A1"))
                .build();

        when(userRepository.findByEmail("customer@moviebooking.com")).thenReturn(Optional.of(testUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));

        assertThrows(BusinessRuleException.class, () ->
                bookingService.createBooking(request, "customer@moviebooking.com")
        );
    }

    @Test
    void testCreateBooking_FailsWhenSeatsAlreadyBooked() {
        BookingRequest request = BookingRequest.builder()
                .showId(1L)
                .numberOfTickets(1)
                .seatNumbers(List.of("A1"))
                .build();

        when(userRepository.findByEmail("customer@moviebooking.com")).thenReturn(Optional.of(testUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));
        when(bookingRepository.findBookedSeatsByShowId(1L)).thenReturn(List.of("A1", "A2"));

        assertThrows(BusinessRuleException.class, () ->
                bookingService.createBooking(request, "customer@moviebooking.com")
        );
    }

    @Test
    void testCreateBooking_FailsWhenTicketCountMismatch() {
        BookingRequest request = BookingRequest.builder()
                .showId(1L)
                .numberOfTickets(2)
                .seatNumbers(List.of("A1"))
                .build();

        when(userRepository.findByEmail("customer@moviebooking.com")).thenReturn(Optional.of(testUser));
        when(showRepository.findById(1L)).thenReturn(Optional.of(testShow));

        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(request, "customer@moviebooking.com")
        );
    }

    @Test
    void testCancelBooking_Success() {
        Booking booking = Booking.builder()
                .id(10L)
                .user(testUser)
                .show(testShow)
                .status(BookingStatus.CONFIRMED)
                .build();

        Payment payment = Payment.builder()
                .id(20L)
                .booking(booking)
                .status(PaymentStatus.COMPLETED)
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBooking_Id(10L)).thenReturn(Optional.of(payment));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(BookingResponse.builder().id(10L).status(BookingStatus.CANCELLED).build());

        BookingResponse response = bookingService.cancelBooking(10L, "customer@moviebooking.com", false);

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testCancelBooking_FailsWhenAlreadyCancelled() {
        Booking booking = Booking.builder()
                .id(10L)
                .user(testUser)
                .show(testShow)
                .status(BookingStatus.CANCELLED)
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(BusinessRuleException.class, () ->
                bookingService.cancelBooking(10L, "customer@moviebooking.com", false)
        );
    }
}
