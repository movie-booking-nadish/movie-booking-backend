package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.ShowRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.ShowResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.BusinessRuleException;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.ShowMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.MovieRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.TheatreRepository;
import lk.ijse.cmjd.movie_booking_backend.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowMapper showMapper;

    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        log.info("Creating show for movie ID: {} and theatre ID: {}", request.getMovieId(), request.getTheatreId());

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.getMovieId()));

        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + request.getTheatreId()));

        if (movie.getStatus() == MovieStatus.ENDED) {
            log.warn("Cannot schedule show: Movie with id {} is ENDED", movie.getId());
            throw new BusinessRuleException("Cannot schedule show for a movie with status ENDED");
        }

        if (theatre.getStatus() != TheatreStatus.ACTIVE) {
            log.warn("Cannot schedule show: Theatre with id {} is not ACTIVE", theatre.getId());
            throw new BusinessRuleException("Cannot schedule show in an INACTIVE theatre");
        }

        if (showRepository.existsConflictingShow(theatre.getId(), request.getShowDate(), request.getShowTime(), null)) {
            log.warn("Conflicting show in theatre {} at {} {}", theatre.getName(), request.getShowDate(), request.getShowTime());
            throw new DuplicateResourceException("A show is already scheduled in theatre '" + theatre.getName() + "' on "
                    + request.getShowDate() + " at " + request.getShowTime());
        }

        Show show = showMapper.toEntity(request, movie, theatre);
        Show savedShow = showRepository.save(show);
        log.info("Successfully created show with id: {}", savedShow.getId());
        return showMapper.toResponse(savedShow);
    }

    @Override
    @Transactional
    public ShowResponse updateShow(Long id, ShowRequest request) {
        log.info("Updating show with id: {}", id);

        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.getMovieId()));

        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + request.getTheatreId()));

        if (movie.getStatus() == MovieStatus.ENDED) {
            log.warn("Cannot schedule show: Movie with id {} is ENDED", movie.getId());
            throw new BusinessRuleException("Cannot schedule show for a movie with status ENDED");
        }

        if (theatre.getStatus() != TheatreStatus.ACTIVE) {
            log.warn("Cannot schedule show: Theatre with id {} is not ACTIVE", theatre.getId());
            throw new BusinessRuleException("Cannot schedule show in an INACTIVE theatre");
        }

        if (showRepository.existsConflictingShow(theatre.getId(), request.getShowDate(), request.getShowTime(), id)) {
            log.warn("Conflicting show in theatre {} at {} {}", theatre.getName(), request.getShowDate(), request.getShowTime());
            throw new DuplicateResourceException("A show is already scheduled in theatre '" + theatre.getName() + "' on "
                    + request.getShowDate() + " at " + request.getShowTime());
        }

        showMapper.updateEntity(show, request, movie, theatre);
        Show updatedShow = showRepository.save(show);
        log.info("Successfully updated show with id: {}", updatedShow.getId());
        return showMapper.toResponse(updatedShow);
    }

    @Override
    @Transactional
    public void deleteShow(Long id) {
        log.info("Deleting show with id: {}", id);
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        showRepository.delete(show);
        log.info("Successfully deleted show with id: {}", id);
    }

    @Override
    public ShowResponse getShowById(Long id) {
        log.info("Fetching show with id: {}", id);
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        return showMapper.toResponse(show);
    }

    @Override
    public List<ShowResponse> getAllShows(Long movieId, Long theatreId, LocalDate showDate, ShowStatus status) {
        log.info("Fetching shows with filters - movieId: {}, theatreId: {}, date: {}, status: {}",
                movieId, theatreId, showDate, status);
        List<Show> shows = showRepository.findShowsWithFilters(movieId, theatreId, showDate, status);
        return shows.stream()
                .map(showMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowResponse> getShowsByMovieId(Long movieId) {
        log.info("Fetching shows for movie id: {}", movieId);
        List<Show> shows = showRepository.findByMovie_Id(movieId);
        return shows.stream()
                .map(showMapper::toResponse)
                .collect(Collectors.toList());
    }
}
