package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchShowDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ShowService {

    /**
     * Find all show entries ordered by date and time.
     *
     * @return ordered list of all show entries
     */
    List<Show> findAll();

    /**
     * Find a single show entry by id.
     *
     * @param id the id of the show entry
     * @return the show entry
     */
    Show findOne(Long id);

    /**
     * Create a single show entry.
     *
     * @param show to create
     * @return created show entry
     */
    Show createShow(Show show) throws ConflictException;

    /**
     * Save an image and return the URL.
     *
     * @param image to save
     * @return the URL of the saved image
     */
    String saveImage(MultipartFile image);

    /**
     * Find all shows without event.
     *
     * @return list of shows without event
     */
    List<Show> findShowsWithoutEvent(String searchQuery, LocalDate dateFrom, LocalDate dateTo);

    /**
     * Get all shows matching the filter.
     *
     * @param searchShowDto search parameters
     * @return the list of matching shows
     */
    List<Show> showsByFilter(SearchShowDto searchShowDto);


    /**
     * Find all shows by their IDs.
     *
     * @param ids list of show IDs to fetch
     * @return list of shows matching the provided IDs
     */
    List<Show> findAllByIds(List<Long> ids);


    /**
     * Find all shows for a specific hall.
     *
     * @param hallId the id of the hall
     * @return list of shows in that hall
     */
    List<Show> findShowsByHallId(Long hallId);

}