package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Hall} entities.
 * Provides methods for CRUD operations and custom queries related to venue halls.
 * Halls represent performance spaces within venues, containing both seated and standing sectors.
 */
@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    /**
     * Find all halls by their IDs.
     *
     * @param ids list of hall IDs to find
     * @return list of halls matching the provided IDs
     */
    List<Hall> findAllById(Iterable<Long> ids);

    /**
     * Find all halls for a specific venue, including their sectors and seats.
     * Eagerly fetches related entities to avoid N+1 query problems.
     *
     * @param venueId the ID of the venue
     * @return list of halls with their complete sector and seat information
     */
    @Query("SELECT h FROM Hall h "
        + "LEFT JOIN FETCH h.sectors s "
        + "LEFT JOIN FETCH s.seats "
        + "LEFT JOIN FETCH h.standingSectors "
        + "WHERE h.venue.id = :venueId")
    List<Hall> findByVenueId(@Param("venueId") Long venueId);

    /**
     * Find a standing sector by its ID.
     * Used for operations specific to standing areas within halls.
     *
     * @param id the ID of the standing sector
     * @return Optional containing the standing sector if found, empty otherwise
     */
    @Query("SELECT ss FROM StandingSector ss WHERE ss.id = :id")
    Optional<StandingSector> findStandingSectorById(@Param("id") Long id);
}