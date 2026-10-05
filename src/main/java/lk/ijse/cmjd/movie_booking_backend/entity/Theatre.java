package lk.ijse.cmjd.movie_booking_backend.entity;

import jakarta.persistence.*;
import lk.ijse.cmjd.movie_booking_backend.enums.TheatreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TheatreStatus status;
}
