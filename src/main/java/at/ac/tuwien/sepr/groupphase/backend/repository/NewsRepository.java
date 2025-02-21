package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link News} entities.
 * Provides methods for CRUD operations and custom queries related to news management.
 * News entries are used to communicate updates, announcements, and other information to users.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @return ordered list of al message entries
     */
    List<News> findAllByOrderByPublishedAtDesc();

}
