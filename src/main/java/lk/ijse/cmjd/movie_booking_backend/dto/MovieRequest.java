package lk.ijse.cmjd.movie_booking_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MovieRequest {

    @NotBlank(message = "Movie title is required")
    private String title;

    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @NotNull(message = "Status is required")
    private MovieStatus status;

    private String posterUrl;
}
