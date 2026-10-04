package lk.ijse.cmjd.movie_booking_backend.repository;

import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE " +
           "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:language IS NULL OR LOWER(m.language) = LOWER(:language)) AND " +
           "(:genre IS NULL OR LOWER(m.genre) = LOWER(:genre)) AND " +
           "(:status IS NULL OR m.status = :status)")
    List<Movie> searchMovies(
            @Param("title") String title,
            @Param("language") String language,
            @Param("genre") String genre,
            @Param("status") MovieStatus status
    );
}
