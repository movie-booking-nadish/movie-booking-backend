package lk.ijse.cmjd.movie_booking_backend.service;

import lk.ijse.cmjd.movie_booking_backend.dto.MovieRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.MovieResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.MovieMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.MovieRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = Movie.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .duration(148)
                .language("English")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .status(MovieStatus.NOW_SHOWING)
                .build();
    }

    @Test
    void testCreateMovie_Success() {
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .duration(148)
                .language("English")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .status(MovieStatus.NOW_SHOWING)
                .build();

        when(movieMapper.toEntity(request)).thenReturn(testMovie);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);
        when(movieMapper.toResponse(testMovie)).thenReturn(MovieResponse.builder().id(1L).title("Inception").build());

        MovieResponse response = movieService.createMovie(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testDeleteMovie_FailsWhenShowsExist() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(showRepository.existsByMovie_Id(1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                movieService.deleteMovie(1L)
        );
        verify(movieRepository, never()).delete(any(Movie.class));
    }

    @Test
    void testDeleteMovie_SuccessWhenNoShows() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(showRepository.existsByMovie_Id(1L)).thenReturn(false);

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).delete(testMovie);
    }

    @Test
    void testGetMovieById_NotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                movieService.getMovieById(99L)
        );
    }
}
