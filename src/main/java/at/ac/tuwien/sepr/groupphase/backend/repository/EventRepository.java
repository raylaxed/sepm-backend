package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Event} entities.
 * Provides methods for CRUD operations and custom queries related to event management.
 * Events represent series of shows that can span multiple dates and venues.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find top 10 future or ongoing events ordered by number of sold seats.
     * Used for displaying popular events on the platform.
     *
     * @return list of the 10 most popular future or ongoing events
     */
    @Query("""
        SELECT e FROM Event e
        WHERE e.durationTo >= CURRENT_DATE
        ORDER BY e.soldSeats DESC
        LIMIT 10
        """)
    List<Event> findTop10ByOrderBySoldSeatsDesc();

    /**
     * Find top 10 future or ongoing events of a specific type ordered by number of sold seats.
     * Used for displaying popular events within a specific category.
     *
     * @param type the event type to filter by
     * @return list of the 10 most popular future or ongoing events of the specified type
     */
    @Query("""
        SELECT e FROM Event e
        WHERE e.type = :type
        AND e.durationTo >= CURRENT_DATE
        ORDER BY e.soldSeats DESC
        LIMIT 10
        """)
    List<Event> findTop10ByTypeOrderBySoldSeatsDesc(@Param("type") String type);

    /**
     * Find all future or ongoing events ordered by sold seats.
     * Eagerly fetches related shows and artists to avoid N+1 query problems.
     *
     * @return list of all future or ongoing events, ordered by popularity
     */
    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN FETCH e.shows s
        LEFT JOIN FETCH s.artists
        WHERE e.durationTo >= CURRENT_DATE
        ORDER BY e.soldSeats DESC
        """)
    List<Event> findAllByOrderBySoldSeatsDesc();

    /**
     * Find future or ongoing events by multiple filter criteria.
     * All filter parameters are optional and can be null.
     *
     * @param name event name filter (partial match, case-insensitive)
     * @param type event type filter (partial match, case-insensitive)
     * @param text event description filter (partial match, case-insensitive)
     * @param duration approximate show duration in minutes (±30 minutes)
     * @return filtered list of future or ongoing events
     */
    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN FETCH e.shows s
        WHERE e.durationTo >= CURRENT_DATE
        AND (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:type IS NULL OR LOWER(e.type) LIKE LOWER(CONCAT('%', :type, '%')))
        AND (:text IS NULL OR LOWER(e.text) LIKE LOWER(CONCAT('%', :text, '%')))
        AND (:duration IS NULL OR EXISTS (
            SELECT 1 FROM Show s2
            WHERE s2.event = e
            AND s2.duration BETWEEN :duration - 30 AND :duration + 30
        ))
        ORDER BY e.durationFrom ASC
        """)
    List<Event> findByFilters(
        @Param("name") String name,
        @Param("type") String type,
        @Param("text") String text,
        @Param("duration") Integer duration
    );
}
