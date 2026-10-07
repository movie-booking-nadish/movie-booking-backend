package lk.ijse.cmjd.movie_booking_backend.service.impl;

import lk.ijse.cmjd.movie_booking_backend.dto.TheatreRequest;
import lk.ijse.cmjd.movie_booking_backend.dto.TheatreResponse;
import lk.ijse.cmjd.movie_booking_backend.entity.Theatre;
import lk.ijse.cmjd.movie_booking_backend.exception.BadRequestException;
import lk.ijse.cmjd.movie_booking_backend.exception.DuplicateResourceException;
import lk.ijse.cmjd.movie_booking_backend.exception.ResourceNotFoundException;
import lk.ijse.cmjd.movie_booking_backend.mapper.TheatreMapper;
import lk.ijse.cmjd.movie_booking_backend.repository.ShowRepository;
import lk.ijse.cmjd.movie_booking_backend.repository.TheatreRepository;
import lk.ijse.cmjd.movie_booking_backend.service.TheatreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheatreServiceImpl implements TheatreService {

    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final TheatreMapper theatreMapper;

    @Override
    @Transactional
    public TheatreResponse createTheatre(TheatreRequest request) {
        log.info("Creating theatre with name: {}", request.getName());
        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be positive");
        }
        Theatre theatre = theatreMapper.toEntity(request);
        Theatre savedTheatre = theatreRepository.save(theatre);
        log.info("Successfully created theatre with id: {}", savedTheatre.getId());
        return theatreMapper.toResponse(savedTheatre);
    }

    @Override
    @Transactional
    public TheatreResponse updateTheatre(Long id, TheatreRequest request) {
        log.info("Updating theatre with id: {}", id);
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));

        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be positive");
        }

        theatreMapper.updateEntity(theatre, request);
        Theatre updatedTheatre = theatreRepository.save(theatre);
        log.info("Successfully updated theatre with id: {}", updatedTheatre.getId());
        return theatreMapper.toResponse(updatedTheatre);
    }

    @Override
    @Transactional
    public void deleteTheatre(Long id) {
        log.info("Attempting to delete theatre with id: {}", id);
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));

        if (showRepository.existsByTheatre_Id(id)) {
            log.warn("Cannot delete theatre with id: {} as it has associated shows", id);
            throw new DuplicateResourceException("Cannot delete theatre with id " + id + " because it has scheduled shows");
        }

        theatreRepository.delete(theatre);
        log.info("Successfully deleted theatre with id: {}", id);
    }

    @Override
    public TheatreResponse getTheatreById(Long id) {
        log.info("Fetching theatre with id: {}", id);
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
        return theatreMapper.toResponse(theatre);
    }

    @Override
    public List<TheatreResponse> getAllTheatres() {
        log.info("Fetching all theatres");
        return theatreRepository.findAll().stream()
                .map(theatreMapper::toResponse)
                .collect(Collectors.toList());
    }
}
