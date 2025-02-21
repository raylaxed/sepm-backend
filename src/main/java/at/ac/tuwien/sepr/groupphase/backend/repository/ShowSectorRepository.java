package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link ShowSector} entities.
 * Provides methods for CRUD operations and custom queries related to sectors within shows.
 * ShowSector represents the configuration and pricing of a sector for a specific show.
 */
@Repository
public interface ShowSectorRepository extends JpaRepository<ShowSector, Long> {

    /**
     * Find a ShowSector by its associated show ID and sector ID.
     *
     * @param showId the ID of the show
     * @param sectorId the ID of the sector
     * @return Optional containing the ShowSector if found, empty otherwise
     */
    Optional<ShowSector> findByShowIdAndSectorId(Long showId, Long sectorId);

    /**
     * Find a ShowSector by its associated show ID and standing sector ID.
     *
     * @param showId the ID of the show
     * @param standingSectorId the ID of the standing sector
     * @return Optional containing the ShowSector if found, empty otherwise
     */
    Optional<ShowSector> findByShowIdAndStandingSectorId(Long showId, Long standingSectorId);

    /**
     * Retrieves all ShowSector entities where the sector ID or standingSector ID matches the provided ID.
     *
     * @param sectorId the ID of the regular sector
     * @param standingSectorId the ID of the standing sector
     * @return a list of matching ShowSector entities
     */
    List<ShowSector> findBySector_IdOrStandingSector_Id(Long sectorId, Long standingSectorId);
}