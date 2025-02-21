package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Ticket} entities.
 * Provides methods for CRUD operations and custom queries related to ticket management.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find all tickets associated with a specific user.
     *
     * @param user the user whose tickets to find
     * @return list of tickets belonging to the user
     */
    List<Ticket> findByUser(ApplicationUser user);

    /**
     * Find a specific ticket by its ID and associated user.
     *
     * @param id the ticket ID
     * @param user the user who owns the ticket
     * @return Optional containing the ticket if found, empty otherwise
     */
    Optional<Ticket> findByIdAndUser(Long id, ApplicationUser user);

    /**
     * Find all tickets associated with a specific order.
     *
     * @param orderId the ID of the order
     * @return list of tickets in the order
     */
    List<Ticket> findByOrderId(Long orderId);

    /**
     * Delete all reserved tickets for a specific user.
     * Used for cleanup of expired reservations.
     *
     * @param user the user whose reserved tickets should be deleted
     */
    void deleteByUserAndReservedTrue(ApplicationUser user);

    /**
     * Delete all cart tickets for a specific user.
     * Used for cleanup of abandoned cart items.
     *
     * @param user the user whose cart tickets should be deleted
     */
    void deleteByUserAndInCartTrue(ApplicationUser user);

    /**
     * Find all purchased tickets for a specific user.
     *
     * @param user the user whose purchased tickets to find
     * @return list of purchased tickets
     */
    List<Ticket> findByUserAndPurchasedTrue(ApplicationUser user);

    /**
     * Count tickets in a standing sector for a specific show that are either in cart, reserved, or purchased.
     *
     * @param sectorId the ID of the standing sector
     * @param showId the ID of the show
     * @return count of tickets in the specified states
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.standingSector.id = :sectorId AND t.show.id = :showId AND (t.inCart = true OR t.reserved = true OR t.purchased = true)")
    Long countByStandingSectorIdAndShowIdAndInCartTrueOrReservedTrueOrPurchasedTrue(
        @Param("sectorId") Long sectorId,
        @Param("showId") Long showId);
}