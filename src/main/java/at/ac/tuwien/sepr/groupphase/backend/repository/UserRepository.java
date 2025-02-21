package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link ApplicationUser} entities.
 * This interface provides CRUD operations and custom queries for user data persistence.
 */
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the user with the specified email address, or null if not found
     */
    ApplicationUser findByEmail(String email);

    /**
     * Finds all users with the specified blocked status.
     *
     * @param blocked the blocked status to search for
     * @return a list of users with the specified blocked status
     */
    List<ApplicationUser> findByBlocked(Boolean blocked);

    /**
     * Finds all users in the database.
     *
     * @return a list of all users
     */
    List<ApplicationUser> findAll();
}
