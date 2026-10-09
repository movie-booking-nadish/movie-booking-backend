package lk.ijse.cmjd.movie_booking_backend.config;

import lk.ijse.cmjd.movie_booking_backend.entity.Movie;
import lk.ijse.cmjd.movie_booking_backend.entity.Show;
import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import lk.ijse.cmjd.movie_booking_backend.entity.User;
import lk.ijse.cmjd.movie_booking_backend.enums.MovieStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.Role;
import lk.ijse.cmjd.movie_booking_backend.enums.ShowStatus;
import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import lk.ijse.cmjd.movie_booking_backend.repository.MovieRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.TheatreRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data seeding.");
            return;
        }

        log.info("Database is empty. Seeding initial data...");

        // 1. Seed Users
        User admin = User.builder()
                .fullName("Admin User")
                .email("admin@moviebooking.com")
                .password(passwordEncoder.encode("Admin@123"))
                .phone("0771234567")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        User customer = User.builder()
                .fullName("Customer User")
                .email("customer@moviebooking.com")
                .password(passwordEncoder.encode("Customer@123"))
                .phone("0777654321")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.saveAll(List.of(admin, customer));
        log.info("Seeded 2 users (1 admin, 1 customer)");

        // 2. Seed Theatres
        Theatre theatre1 = Theatre.builder()
                .name("Savoy Cinema")
                .location("Wellawatte, Colombo")
                .capacity(60)
                .status(TheatreStatus.ACTIVE)
                .build();

        Theatre theatre2 = Theatre.builder()
                .name("Majestic Cineplex")
                .location("Bambalapitiya, Colombo")
                .capacity(50)
                .status(TheatreStatus.ACTIVE)
                .build();

        List<Theatre> savedTheatres = theatreRepository.saveAll(List.of(theatre1, theatre2));
        log.info("Seeded 2 theatres");

        // 3. Seed Movies (5 movies with mixed statuses)
        Movie movie1 = Movie.builder()
                .title("Inception")
                .description("A thief who steals corporate secrets through dream-sharing technology is given an inverse task.")
                .duration(148)
                .language("English")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.now().minusMonths(6))
                .status(MovieStatus.NOW_SHOWING)
                .posterUrl("https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500")
                .build();

        Movie movie2 = Movie.builder()
                .title("Interstellar")
                .description("A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.")
                .duration(169)
                .language("English")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.now().minusMonths(3))
                .status(MovieStatus.NOW_SHOWING)
                .posterUrl("https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500")
                .build();

        Movie movie3 = Movie.builder()
                .title("Avatar: The Way of Water")
                .description("Jake Sully lives with his newfound family formed on the extrasolar moon Pandora.")
                .duration(192)
                .language("English")
                .genre("Action")
                .releaseDate(LocalDate.now().plusMonths(1))
                .status(MovieStatus.UPCOMING)
                .posterUrl("https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500")
                .build();

        Movie movie4 = Movie.builder()
                .title("Dune: Part Two")
                .description("Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators.")
                .duration(166)
                .language("English")
                .genre("Sci-Fi")
                .releaseDate(LocalDate.now().plusMonths(2))
                .status(MovieStatus.UPCOMING)
                .posterUrl("https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=500")
                .build();

        Movie movie5 = Movie.builder()
                .title("Avengers: Endgame")
                .description("After the devastating events of Infinity War, the universe is in ruins.")
                .duration(181)
                .language("English")
                .genre("Action")
                .releaseDate(LocalDate.now().minusYears(1))
                .status(MovieStatus.ENDED)
                .posterUrl("https://images.unsplash.com/photo-1509281373149-e957c6296406?w=500")
                .build();

        List<Movie> savedMovies = movieRepository.saveAll(List.of(movie1, movie2, movie3, movie4, movie5));
        log.info("Seeded 5 movies");

        // 4. Seed Shows over next days
        Show show1 = Show.builder()
                .movie(savedMovies.get(0)) // Inception
                .theatre(savedTheatres.get(0)) // Savoy
                .showDate(LocalDate.now().plusDays(1))
                .showTime(LocalTime.of(14, 30))
                .ticketPrice(new BigDecimal("1200.00"))
                .status(ShowStatus.SCHEDULED)
                .build();

        Show show2 = Show.builder()
                .movie(savedMovies.get(0)) // Inception
                .theatre(savedTheatres.get(1)) // Majestic
                .showDate(LocalDate.now().plusDays(2))
                .showTime(LocalTime.of(18, 0))
                .ticketPrice(new BigDecimal("1500.00"))
                .status(ShowStatus.SCHEDULED)
                .build();

        Show show3 = Show.builder()
                .movie(savedMovies.get(1)) // Interstellar
                .theatre(savedTheatres.get(0)) // Savoy
                .showDate(LocalDate.now().plusDays(1))
                .showTime(LocalTime.of(19, 0))
                .ticketPrice(new BigDecimal("1400.00"))
                .status(ShowStatus.SCHEDULED)
                .build();

        Show show4 = Show.builder()
                .movie(savedMovies.get(1)) // Interstellar
                .theatre(savedTheatres.get(1)) // Majestic
                .showDate(LocalDate.now().plusDays(3))
                .showTime(LocalTime.of(15, 30))
                .ticketPrice(new BigDecimal("1350.00"))
                .status(ShowStatus.SCHEDULED)
                .build();

        showRepository.saveAll(List.of(show1, show2, show3, show4));
        log.info("Seeded 4 shows over the coming days");
        log.info("Sample data seeding completed successfully.");
    }
}
