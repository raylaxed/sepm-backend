package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link Venue} entities.
 * Provides methods for CRUD operations and custom queries related to venue management.
 */
@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    /**
     * Find venues whose names contain the specified query string, ignoring case.
     *
     * @param query the string to search for within venue names
     * @return a list of venues whose names contain the query string
     */
    List<Venue> findByNameContainingIgnoreCase(String query);

    /**
     * Find venues based on multiple filter criteria.
     * All parameters are optional and can be null. If a parameter is null, it will not be used in the filter.
     *
     * @param name the name of the venue (partial match, case-insensitive)
     * @param street the street address (partial match, case-insensitive)
     * @param city the city name (partial match, case-insensitive)
     * @param county the county name (partial match, case-insensitive)
     * @param postalCode the postal code (partial match, case-insensitive)
     * @return a list of venues matching all non-null filter criteria
     */
    @Query("SELECT DISTINCT v FROM Venue v LEFT JOIN FETCH v.hallIds WHERE "
        + "(:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
        + "(:street IS NULL OR LOWER(v.street) LIKE LOWER(CONCAT('%', :street, '%'))) AND "
        + "(:city IS NULL OR LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND "
        + "(:county IS NULL OR LOWER(v.county) LIKE LOWER(CONCAT('%', :county, '%'))) AND "
        + "(:postalCode IS NULL OR LOWER(v.postalCode) LIKE LOWER(CONCAT('%', :postalCode, '%')))")
    List<Venue> findByFilter(@Param("name") String name,
                            @Param("street") String street,
                            @Param("city") String city,
                            @Param("county") String county,
                            @Param("postalCode") String postalCode);

    @Query("SELECT DISTINCT v.city FROM Venue v WHERE v.city IS NOT NULL")
    List<String> findDistinctCities();

    @Query("SELECT DISTINCT v.county FROM Venue v WHERE v.county IS NOT NULL")
    List<String> findDistinctCountries();

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Show s WHERE s.venue.id = :venueId")
    boolean hasAssociatedShows(@Param("venueId") Long venueId);
}