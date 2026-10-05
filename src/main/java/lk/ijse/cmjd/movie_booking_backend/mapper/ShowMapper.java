package lk.ijse.cmjd.movie_booking_backend.mapper;

import lk.ijse.cmjd.movie_booking_backend.dto.ShowRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.ShowResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import org.springframework.stereotype.Component;

@Component
public class ShowMapper {

    public Show toEntity(ShowRequest request, Movie movie, Theatre theatre) {
        if (request == null) return null;
        return Show.builder()
                .movie(movie)
                .theatre(theatre)
                .showDate(request.getShowDate())
                .showTime(request.getShowTime())
                .ticketPrice(request.getTicketPrice())
                .status(request.getStatus())
                .build();
    }

    public ShowResponse toResponse(Show show) {
        if (show == null) return null;
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie() != null ? show.getMovie().getId() : null)
                .movieTitle(show.getMovie() != null ? show.getMovie().getTitle() : null)
                .theatreId(show.getTheatre() != null ? show.getTheatre().getId() : null)
                .theatreName(show.getTheatre() != null ? show.getTheatre().getName() : null)
                .showDate(show.getShowDate())
                .showTime(show.getShowTime())
                .ticketPrice(show.getTicketPrice())
                .status(show.getStatus())
                .build();
    }

    public void updateEntity(Show show, ShowRequest request, Movie movie, Theatre theatre) {
        if (show == null || request == null) return;
        show.setMovie(movie);
        show.setTheatre(theatre);
        show.setShowDate(request.getShowDate());
        show.setShowTime(request.getShowTime());
        show.setTicketPrice(request.getTicketPrice());
        show.setStatus(request.getStatus());
    }
}
