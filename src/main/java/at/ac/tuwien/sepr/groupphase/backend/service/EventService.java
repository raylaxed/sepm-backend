package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    /**
     * Create a new event entry.
     *
     * @param event the event to create
     * @return the created event entry
     */
    Event createEvent(Event event);


    /**
     * Save an image.
     *
     * @param file the image to save
     * @return the Url of the image
     */
    String saveImage(MultipartFile file);


    /**
     * Get all events.
     *
     * @return the list of events
     */
    List<Event> findAll();

    /**
     * Get an event by id.
     *
     * @param id the id of the event
     * @return the event
     */
    Event findOne(Long id);

    /**
     * Get top 10 events ordered by sold seats.
     *
     * @return the list of top 10 events
     */
    List<Event> findTop10BySoldSeats(String eventType);

    /**
     * Get all events matching the filter.
     *
     * @param searchEventDto search parameters
     * @return the list of the matching events
     */

    List<Event> eventsByFilter(SearchEventDto searchEventDto);

}
