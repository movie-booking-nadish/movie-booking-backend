package lk.ijse.cmjd.movie_booking_backend.repository;

import lk.ijse.cmjd.movie_booking_backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_IdOrderByBookingDateDesc(Long userId);

    @Query("SELECT s FROM Booking b JOIN b.seatNumbers s WHERE b.show.id = :showId AND b.status != lk.ijse.cmjd.movie_booking_backend.enums.BookingStatus.CANCELLED")
    List<String> findBookedSeatsByShowId(@Param("showId") Long showId);
}
