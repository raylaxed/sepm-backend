package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link Order} entities.
 * Provides methods for CRUD operations and custom queries related to ticket orders.
 * Orders represent ticket purchases made by users, including both active and cancelled orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders for a specific user.
     * Retrieves both active and cancelled orders.
     *
     * @param user the user whose orders to retrieve
     * @return list of all orders for the given user
     */
    List<Order> findByUser(ApplicationUser user);

    /**
     * Find all non-cancelled orders for a specific user.
     * Only retrieves orders that are currently active (not cancelled).
     *
     * @param user the user whose orders to retrieve
     * @return list of all active (non-cancelled) orders for the given user
     */
    List<Order> findByUserAndCancelledFalse(ApplicationUser user);

    /**
     * Find all cancelled orders for a specific user.
     *
     * @param user the user whose cancelled orders to retrieve
     * @return list of all cancelled orders for the given user
     */
    List<Order> findByUserAndCancelledTrue(ApplicationUser user);
}