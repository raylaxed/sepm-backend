package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link ApplicationUser} entities.
 * Provides methods for CRUD operations and custom queries related to user management.
 */
@Repository
public interface UserRepositoryInterface extends JpaRepository<ApplicationUser, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the user with the given email address, or null if not found
     */
    ApplicationUser findUserByEmail(String email);

    /**
     * Finds a user by their email address, ignoring case sensitivity.
     *
     * @param email the email address to search for (case-insensitive)
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<ApplicationUser> findByEmailIgnoreCase(String email);
}