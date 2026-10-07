package lk.ijse.cmjd.movie_booking_backend.repository;

import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovie_Id(Long movieId);
    boolean existsByMovie_Id(Long movieId);
    boolean existsByTheatre_Id(Long theatreId);

    @Query("SELECT s FROM Show s WHERE " +
           "(:movieId IS NULL OR s.movie.id = :movieId) AND " +
           "(:theatreId IS NULL OR s.theatre.id = :theatreId) AND " +
           "(:showDate IS NULL OR s.showDate = :showDate) AND " +
           "(:status IS NULL OR s.status = :status)")
    List<Show> findShowsWithFilters(
            @Param("movieId") Long movieId,
            @Param("theatreId") Long theatreId,
            @Param("showDate") LocalDate showDate,
            @Param("status") ShowStatus status
    );

    @Query("SELECT COUNT(s) > 0 FROM Show s WHERE " +
           "s.theatre.id = :theatreId AND " +
           "s.showDate = :showDate AND " +
           "s.showTime = :showTime AND " +
           "s.status != lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus.CANCELLED AND " +
           "(:excludeShowId IS NULL OR s.id != :excludeShowId)")
    boolean existsConflictingShow(
            @Param("theatreId") Long theatreId,
            @Param("showDate") LocalDate showDate,
            @Param("showTime") LocalTime showTime,
            @Param("excludeShowId") Long excludeShowId
    );
}
