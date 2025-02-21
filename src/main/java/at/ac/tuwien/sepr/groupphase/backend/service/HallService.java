package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface HallService {
    /**
     * Find all halls.
     *
     * @return list of all halls
     */
    List<Hall> findAll();

    /**
     * Find a specific hall by id.
     *
     * @param id the id of the hall to find
     * @return the hall
     */
    Hall findOne(Long id);

    /**
     * Create a new hall.
     *
     * @param hallDto the hall to create
     * @return the created hall
     */
    Hall createHall(HallDto hallDto);

    /**
     * Update an existing hall.
     *
     * @param hallDto the hall to update
     * @return the updated hall
     * @throws ValidationException if the hall is associated with shows
     */
    Hall updateHall(HallDto hallDto) throws ValidationException;

    /**
     * Delete a hall.
     *
     * @param id the id of the hall to delete
     */
    void deleteHall(Long id);

    /**
     * Find seats by ids.
     *
     * @param seatIds the ids of the seats to find
     * @return the seats
     */
    List<Seat> findSeatsById(List<Long> seatIds);

    /**
     * Find sectors by ids.
     *
     * @param sectorIds the ids of the sectors to find
     * @return the sectors
     */
    List<Sector> findSectorsById(List<Long> sectorIds);

    /**
     * Find standing sectors by ids.
     *
     * @param sectorIds the ids of the standing sectors to find
     * @return the standing sectors
     */
    List<StandingSector> findStandingSectorsById(List<Long> sectorIds);

    /**
     * Remove all content related to a hall (sectors, standing sectors, and seats).
     *
     * @param hallId the id of the hall whose content should be deleted
     * @throws ValidationException if the hall is associated with shows
     */
    void removeHallContent(Long hallId) throws ValidationException;
}
