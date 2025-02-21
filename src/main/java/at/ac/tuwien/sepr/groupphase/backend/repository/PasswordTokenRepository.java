package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link PasswordResetToken} entities.
 * Provides methods for CRUD operations and custom queries related to password reset tokens.
 */
@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find a password reset token by its token string.
     *
     * @param token the token string to search for
     * @return the password reset token if found, null otherwise
     */
    PasswordResetToken findByToken(String token);

    /**
     * Find a password reset token by its associated user.
     *
     * @param user the user whose token to find
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<PasswordResetToken> findByUser(ApplicationUser user);

    /**
     * Delete all password reset tokens for a specific user.
     * Used when cleaning up expired tokens or after successful password reset.
     *
     * @param user the user whose tokens should be deleted
     */
    void deleteByUser(ApplicationUser user);
}