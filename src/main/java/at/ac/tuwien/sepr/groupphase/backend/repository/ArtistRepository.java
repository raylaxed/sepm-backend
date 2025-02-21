package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Artist} entities.
 * Provides methods for CRUD operations and custom queries related to artist management.
 * Artists are performers who can be associated with shows and events.
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    /**
     * Find an artist by ID and fetch their upcoming shows.
     * Only includes shows that haven't happened yet (future shows).
     *
     * @param id the ID of the artist to find
     * @return Optional containing the artist with their upcoming shows if found, empty otherwise
     */
    @Query("""
        SELECT DISTINCT a FROM Artist a 
        LEFT JOIN FETCH a.shows s 
        LEFT JOIN FETCH s.artists 
        WHERE a.id = :id 
        AND (s IS NULL OR s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.time > CURRENT_TIME))
        """)
    Optional<Artist> findByIdWithShows(@Param("id") Long id);
    
    /**
     * Find artists whose names contain the specified query, ignoring case.
     * Only includes future shows in the results.
     *
     * @param query the string to search for within artist names (case-insensitive)
     * @return list of artists whose names contain the query string
     */
    List<Artist> findByNameContainingIgnoreCase(String query);

}