package lk.ijse.cmjd.movie_booking_backend.repository;

import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByStatus(TheatreStatus status);
}
