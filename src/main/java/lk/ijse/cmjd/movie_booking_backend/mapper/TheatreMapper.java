package lk.ijse.cmjd.movie_booking_backend.mapper;

import lk.ijse.cmjd.movie_booking_backend.dto.TheatreRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.TheatreResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import org.springframework.stereotype.Component;

@Component
public class TheatreMapper {

    public Theatre toEntity(TheatreRequest request) {
        if (request == null) return null;
        return Theatre.builder()
                .name(request.getName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .status(request.getStatus())
                .build();
    }

    public TheatreResponse toResponse(Theatre theatre) {
        if (theatre == null) return null;
        return TheatreResponse.builder()
                .id(theatre.getId())
                .name(theatre.getName())
                .location(theatre.getLocation())
                .capacity(theatre.getCapacity())
                .status(theatre.getStatus())
                .build();
    }

    public void updateEntity(Theatre theatre, TheatreRequest request) {
        if (theatre == null || request == null) return;
        theatre.setName(request.getName());
        theatre.setLocation(request.getLocation());
        theatre.setCapacity(request.getCapacity());
        theatre.setStatus(request.getStatus());
    }
}
