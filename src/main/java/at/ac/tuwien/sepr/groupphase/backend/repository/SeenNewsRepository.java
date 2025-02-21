package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SeenNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link SeenNews} entities.
 * Provides methods for tracking which news articles have been viewed by users.
 * This helps in managing the read/unread status of news items for each user.
 */
@Repository
public interface SeenNewsRepository extends JpaRepository<SeenNews, Long> {

    /**
     * Find the News by User.
     *
     * @param user the current user
     * @return List of News entries the user has already seen
     */
    Optional<SeenNews> findByUser(ApplicationUser user);

    /**
     * Delete the sen News by User.
     *
     * @param user the current user
     */
    void deleteByUser(ApplicationUser user);

}
