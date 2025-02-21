package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for managing {@link Show} entities.
 * Provides methods for CRUD operations and custom queries related to show management.
 */
@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    /**
     * Find all shows without event that haven't happened yet and match the search query.
     *
     * @param searchQuery the string to search for in show names (case-insensitive)
     * @return list of future shows without event matching the search query
     */
    @Query("""
        SELECT s FROM Show s
        WHERE s.event IS NULL
        AND LOWER(s.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.time > CURRENT_TIME))
        ORDER BY s.date ASC, s.time ASC
        """)
    List<Show> findAllByEventIsNullAndNameContainingIgnoreCase(@Param("searchQuery") String searchQuery);

    /**
     * Find all shows without event that match the given filter criteria.
     *
     * @param search optional search string for show names
     * @param dateFrom optional start date filter
     * @param dateTo optional end date filter
     * @return filtered list of shows ordered by date and time
     */
    @Query("SELECT s FROM Show s WHERE s.event IS NULL "
        + "AND (:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
        + "AND (:dateFrom IS NULL OR s.date >= :dateFrom) "
        + "AND (:dateTo IS NULL OR s.date <= :dateTo) "
        + "ORDER BY s.date ASC, s.time ASC")
    List<Show> findAllByEventIsNullWithFilters(
        @Param("search") String search,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo);

    /**
     * Find a show by ID with its associated artists and hall details.
     *
     * @param id the show ID
     * @return Optional containing the show with all its relationships if found
     */
    @Query("SELECT DISTINCT s FROM Show s "
        + "LEFT JOIN FETCH s.artists "
        + "LEFT JOIN FETCH s.hall h "
        + "LEFT JOIN FETCH h.sectors "
        + "LEFT JOIN FETCH h.standingSectors "
        + "LEFT JOIN FETCH h.stage "
        + "WHERE s.id = :id")
    Optional<Show> findByIdWithArtists(@Param("id") Long id);

    /**
     * Find shows matching complex filter criteria.
     *
     * @param name optional show name filter
     * @param date optional specific date filter
     * @param timeFrom optional start time filter
     * @param timeTo optional end time filter
     * @param maxPrice optional maximum price filter
     * @param minPrice optional minimum price filter
     * @param eventName optional event name filter
     * @param venueId optional venue ID filter
     * @param type optional show type filter
     * @return filtered list of future shows
     */
    @Query("""
        SELECT DISTINCT s FROM Show s
        LEFT JOIN FETCH s.event e
        LEFT JOIN FETCH s.artists
        WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:date IS NULL OR s.date = :date)
        AND (:timeFrom IS NULL OR s.time >= :timeFrom)
        AND (:timeTo IS NULL OR s.time <= :timeTo)
        AND (:eventName IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :eventName, '%')))
        AND (:venueId IS NULL OR s.venue.id = :venueId)
        AND (:type IS NULL OR s.eventType = :type)
        AND (:minPrice IS NULL OR s.minPrice <= :minPrice)
        AND (:maxPrice IS NULL OR s.maxPrice >= :maxPrice)
        AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.time > CURRENT_TIME))
        ORDER BY s.date ASC, s.time ASC
        """)
    List<Show> findByFilters(
        @Param("name") String name,
        @Param("date") LocalDate date,
        @Param("timeFrom") LocalTime timeFrom,
        @Param("timeTo") LocalTime timeTo,
        @Param("maxPrice") Double maxPrice,
        @Param("minPrice") Double minPrice,
        @Param("eventName") String eventName,
        @Param("venueId") Long venueId,
        @Param("type") String type
    );

    /**
     * Find all future shows ordered by date and time.
     *
     * @return ordered list of all future shows
     */
    @Query("""
        SELECT s FROM Show s
        WHERE (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.time > CURRENT_TIME))
        ORDER BY s.date ASC, s.time ASC
        """)
    List<Show> findAllByOrderByDateAscTimeAsc();

    /**
     * Find shows by IDs with their associated artists.
     *
     * @param ids list of show IDs to fetch
     * @return list of shows with their artists
     */
    @Query("SELECT DISTINCT s FROM Show s "
        + "LEFT JOIN FETCH s.artists "
        + "WHERE s.id IN :ids")
    List<Show> findAllByIdsWithArtists(@Param("ids") List<Long> ids);

    /**
     * Find future shows for a specific hall.
     *
     * @param hallId the ID of the hall
     * @return list of future shows in the specified hall
     */
    @Query("""
        SELECT DISTINCT s FROM Show s
        LEFT JOIN FETCH s.event e
        LEFT JOIN FETCH s.artists
        WHERE s.hall.id = :hallId
        AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.time > CURRENT_TIME))
        ORDER BY s.date ASC, s.time ASC
        """)
    List<Show> findByHallId(@Param("hallId") Long hallId);

    /**
     * Find all shows with their associated show sectors.
     *
     * @return list of shows with show sector information
     */
    @Query("""
        SELECT DISTINCT s FROM Show s
        LEFT JOIN FETCH s.showSectors ss
        LEFT JOIN FETCH ss.sector sec
        LEFT JOIN FETCH ss.standingSector st
        """)
    List<Show> findAllWithShowSectors();

    /**
     * Find sectors with their associated seats.
     *
     * @param sectors set of sectors to fetch seats for
     * @return list of sectors with seat information
     */
    @Query("""
        SELECT DISTINCT s FROM Sector s
        LEFT JOIN FETCH s.seats
        WHERE s IN :sectors
        """)
    List<Sector> findSectorsWithSeats(@Param("sectors") Set<Sector> sectors);

    /**
     * Find shows with their associated show sectors.
     *
     * @param shows list of shows to fetch show sectors for
     * @return list of shows with show sector information
     */
    @Query("""
        SELECT DISTINCT s FROM Show s
        LEFT JOIN FETCH s.showSectors ss
        WHERE s IN :shows
        """)
    List<Show> findShowsWithShowSectors(@Param("shows") List<Show> shows);

    /**
     * Find all shows for a specific venue.
     *
     * @param venueId the ID of the venue
     * @return list of shows at the venue
     */
    @Query("SELECT s FROM Show s WHERE s.venue.id = :venueId")
    List<Show> findByVenueId(@Param("venueId") Long venueId);
}