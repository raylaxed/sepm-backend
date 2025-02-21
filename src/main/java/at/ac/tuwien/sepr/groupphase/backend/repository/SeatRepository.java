package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link Seat} entities.
 * Provides methods for CRUD operations and custom queries related to individual seats in sectors.
 * Seats represent specific seating positions within a sector, each with their own row and number.
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats by their IDs.
     * Used when needing to retrieve multiple seats at once, such as for booking or availability checks.
     *
     * @param ids the collection of seat IDs to fetch
     * @return list of seats matching the provided IDs
     */
    List<Seat> findAllById(Iterable<Long> ids);
} 