package lk.ijse.cmjd.movie_booking_backend.dto;

import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private String language;
    private String genre;
    private LocalDate releaseDate;
    private MovieStatus status;
    private String posterUrl;
}
