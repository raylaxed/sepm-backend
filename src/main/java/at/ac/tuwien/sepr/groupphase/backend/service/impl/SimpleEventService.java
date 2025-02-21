package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SimpleEventService implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventRepository eventRepository;
    private final ShowRepository showRepository;
    private final String imageDirectory;
    private final String imageBaseUrl;

    public SimpleEventService(EventRepository eventRepository,
                              ShowRepository showRepository,
                              @Value("${app.storage.image-directory}") String imageDirectory,
                              @Value("${app.image-base-url}") String imageBaseUrl) {
        this.eventRepository = eventRepository;
        this.showRepository = showRepository;
        this.imageDirectory = imageDirectory;
        this.imageBaseUrl = imageBaseUrl;
    }

    @Override
    public Event createEvent(Event event) {
        LOGGER.debug("Create new event {}", event);

        Event savedEvent = eventRepository.save(event);
        if (event.getShowIds() != null && event.getShowIds().length > 0) {
            List<Show> shows = showRepository.findAllById(Arrays.asList(event.getShowIds()));
            for (Show show : shows) {
                show.setEvent(savedEvent);
            }
            showRepository.saveAll(shows);
        }

        return savedEvent;
    }

    @Override
    public String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Uploaded image is empty");
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(imageDirectory, fileName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, image.getBytes());
            LOGGER.info("Image saved to {}", imagePath);
            return imageBaseUrl + fileName;
        } catch (IOException e) {
            LOGGER.error("Failed to save image", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public List<Event> findAll() {
        LOGGER.debug("Find all events ordered by sold seats");
        return eventRepository.findAllByOrderBySoldSeatsDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Event findOne(Long id) {
        LOGGER.debug("Find event with id {}", id);
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event not found with id " + id));

        // Check if event is in the future or ongoing
        if (event.getDurationTo().isBefore(LocalDate.now())) {
            throw new NotFoundException("Event with id " + id + " has already ended");
        }

        // Filter out past shows if the event has any shows
        if (event.getShows() != null) {
            event.getShows().removeIf(show ->
                show.getDate().isBefore(LocalDate.now())
                    || (show.getDate().isEqual(LocalDate.now()) && show.getTime().isBefore(LocalTime.now()))
            );
        }

        return event;
    }

    @Override
    public List<Event> findTop10BySoldSeats(String eventType) {
        LOGGER.debug("Find top 10 events ordered by sold seats for type {}", eventType);
        return eventType != null ? eventRepository.findTop10ByTypeOrderBySoldSeatsDesc(eventType) : eventRepository.findTop10ByOrderBySoldSeatsDesc();
    }

    @Override
    public List<Event> eventsByFilter(SearchEventDto searchEventDto) {
        LOGGER.debug("Find events by filter {}", searchEventDto);
        return eventRepository.findByFilters(searchEventDto.getName(), searchEventDto.getType(), searchEventDto.getText(), searchEventDto.getDuration());
    }
}
