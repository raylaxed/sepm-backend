package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link StandingSector} entities.
 * Provides methods for CRUD operations and custom queries related to standing sectors in venues.
 * Standing sectors represent areas in a venue where attendees can stand rather than having assigned seats.
 */
@Repository
public interface StandingSectorRepository extends JpaRepository<StandingSector, Long> {
} 