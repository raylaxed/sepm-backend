package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchVenueDto;

import java.util.List;

public interface VenueService {
    /**
     * Find all venue entries.
     *
     * @return list of all venue entries
     */
    List<Venue> findAll();

    /**
     * Find a single venue entry by id.
     *
     * @param id the id of the venue entry
     * @return the venue entry
     */
    Venue findOne(Long id);

    /**
     * Create a new venue entry.
     *
     * @param venueDto to create
     * @return created venue entry
     */
    Venue createVenue(VenueDto venueDto) throws ValidationException, ConflictException;

    /**
     * Update an existing venue entry.
     *
     * @param id the id of the venue to update
     * @param venueDto to update
     * @return updated venue entry
     */
    Venue updateVenue(Long id, VenueDto venueDto) throws ValidationException, ConflictException, NotFoundException;

    /**
     * Delete an existing venue entry.
     *
     * @param id of the venue to delete
     */
    void deleteVenue(Long id) throws ValidationException, NotFoundException;

    /**
     * Search for venues by name.
     *
     * @param query the search term used to find venues
     * @return a list of Venue objects that match the search criteria
     */
    List<Venue> searchVenuesByName(String query);

    /**
     * Get all halls for a specific venue.
     *
     * @param id the id of the venue
     * @return list of halls associated with the venue
     */
    List<Hall> getHallsByVenueId(Long id);

    /**
     * Filter venues based on search criteria.
     *
     * @param searchDto the search criteria
     * @return list of venues matching the criteria
     */
    List<Venue> filterVenues(SearchVenueDto searchDto);

    /**
     * Find unique countries and cities that are already in the db.
     *
     * @return list of String[], one with countries, one with cities
     */
    List<String[]> findUniqueCountriesAndCities();
}