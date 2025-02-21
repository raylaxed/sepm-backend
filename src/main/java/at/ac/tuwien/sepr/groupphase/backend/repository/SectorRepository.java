package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for managing {@link Sector} entities.
 * Provides methods for CRUD operations and custom queries related to seating sectors in venues.
 * Sectors represent designated seating areas within a hall, each containing multiple seats.
 */
@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {

    /**
     * Find all sectors by their IDs and eagerly fetch their associated seats.
     * This is useful when needing complete sector information including seat details.
     *
     * @param ids the collection of sector IDs to fetch
     * @return list of sectors with their associated seats
     */
    @Query("SELECT DISTINCT s FROM Sector s LEFT JOIN FETCH s.seats WHERE s.id IN :ids")
    List<Sector> findAllByIdWithSeats(@Param("ids") Iterable<Long> ids);
} 