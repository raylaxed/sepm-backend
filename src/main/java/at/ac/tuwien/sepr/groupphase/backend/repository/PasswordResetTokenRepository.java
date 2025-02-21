package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Repository for managing {@link PasswordResetToken} entities.
 * Provides methods for CRUD operations and custom queries related to password reset tokens.
 * These tokens are used to securely handle password reset requests and have an expiration time.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find a password reset token by its token string.
     *
     * @param token the unique token string to search for
     * @return Optional containing the token if found, empty otherwise
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Delete all expired password reset tokens.
     * Used for cleanup of tokens that are no longer valid.
     *
     * @param expiryDate the date before which tokens should be considered expired
     */
    void deleteAllByExpiryDateBefore(LocalDateTime expiryDate);

    /**
     * Delete all password reset tokens.
     * Used for maintenance or system cleanup.
     */
    void deleteAll();
}
