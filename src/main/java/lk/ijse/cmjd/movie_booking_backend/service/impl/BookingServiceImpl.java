package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.BookingRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.BookingResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.PaymentStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.BadRequestException;
import lk.ijse.cmjd.movie_booking_backend.exception.BusinessRuleException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.BookingMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.BookingRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.PaymentRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.UserRepository;
import lk.ijse.cmjd.movie_booking_backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String currentUserEmail) {
        log.info("Creating booking for user {} on show ID {}", currentUserEmail, request.getShowId());

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + request.getShowId()));

        if (show.getStatus() != ShowStatus.SCHEDULED) {
            log.warn("Attempt to book non-scheduled show: {}", show.getId());
            throw new BusinessRuleException("Cannot book seats for a show that is not SCHEDULED");
        }

        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new BadRequestException("At least one seat must be selected");
        }

        if (request.getNumberOfTickets() == null || request.getNumberOfTickets() != request.getSeatNumbers().size()) {
            throw new BadRequestException("Number of tickets (" + request.getNumberOfTickets()
                    + ") must match the count of selected seats (" + request.getSeatNumbers().size() + ")");
        }

        Set<String> uniqueSeats = new HashSet<>(request.getSeatNumbers());
        if (uniqueSeats.size() != request.getSeatNumbers().size()) {
            throw new BadRequestException("Duplicate seats found in booking request");
        }

        int capacity = show.getTheatre().getCapacity();
        for (String seat : request.getSeatNumbers()) {
            if (!isValidSeatLabel(seat, capacity)) {
                throw new BadRequestException("Invalid seat label '" + seat + "' for theatre capacity of " + capacity);
            }
        }

        List<String> bookedSeats = bookingRepository.findBookedSeatsByShowId(show.getId());
        for (String seat : request.getSeatNumbers()) {
            if (bookedSeats.contains(seat)) {
                log.warn("Seat {} already booked for show {}", seat, show.getId());
                throw new BusinessRuleException("Seat " + seat + " is already booked");
            }
        }

        BigDecimal totalAmount = show.getTicketPrice().multiply(BigDecimal.valueOf(request.getNumberOfTickets()));
        Booking booking = bookingMapper.toEntity(request, user, show, totalAmount);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully created booking ID {} with total amount {}", savedBooking.getId(), totalAmount);

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long id, String currentUserEmail, boolean isAdmin) {
        log.info("Fetching booking with ID: {} for user {}", id, currentUserEmail);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (!isAdmin && (booking.getUser() == null || !booking.getUser().getEmail().equalsIgnoreCase(currentUserEmail))) {
            throw new AccessDeniedException("You do not have permission to view this booking");
        }

        return bookingMapper.toResponse(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String currentUserEmail) {
        log.info("Fetching bookings for user: {}", currentUserEmail);
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        return bookingRepository.findByUser_IdOrderByBookingDateDesc(user.getId()).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public lk.ijse.cmjd.movie_booking_backend.dto.PageResponse<BookingResponse> getMyBookingsPaginated(String currentUserEmail, int page, int size) {
        List<BookingResponse> all = getMyBookings(currentUserEmail);
        return lk.ijse.cmjd.movie_booking_backend.dto.PageResponse.fromList(all, page, size);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id, String currentUserEmail, boolean isAdmin) {
        log.info("Cancelling booking with ID: {} requested by user {}", id, currentUserEmail);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (!isAdmin && (booking.getUser() == null || !booking.getUser().getEmail().equalsIgnoreCase(currentUserEmail))) {
            throw new AccessDeniedException("You do not have permission to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("Booking is already cancelled");
        }

        if (booking.getShow() != null && booking.getShow().getStatus() == ShowStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot cancel booking for a completed show");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        paymentRepository.findByBooking_Id(booking.getId()).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                log.info("Payment ID {} refunded for cancelled booking ID {}", payment.getId(), booking.getId());
            }
        });

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully cancelled booking ID: {}", savedBooking.getId());
        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings (Admin)");
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public lk.ijse.cmjd.movie_booking_backend.dto.PageResponse<BookingResponse> getAllBookingsPaginated(int page, int size) {
        List<BookingResponse> all = getAllBookings();
        return lk.ijse.cmjd.movie_booking_backend.dto.PageResponse.fromList(all, page, size);
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        log.info("Updating booking status for ID: {} to {}", id, status);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        booking.setStatus(status);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully updated booking ID {} to status {}", savedBooking.getId(), status);
        return bookingMapper.toResponse(savedBooking);
    }

    private boolean isValidSeatLabel(String seat, int capacity) {
        if (seat == null || seat.length() < 2) return false;
        char rowChar = Character.toUpperCase(seat.charAt(0));
        if (rowChar < 'A' || rowChar > 'Z') return false;
        int rowIndex = rowChar - 'A';
        try {
            int seatNumberInRow = Integer.parseInt(seat.substring(1));
            if (seatNumberInRow < 1 || seatNumberInRow > 10) return false;
            int overallSeatIndex = (rowIndex * 10) + seatNumberInRow;
            return overallSeatIndex >= 1 && overallSeatIndex <= capacity;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
