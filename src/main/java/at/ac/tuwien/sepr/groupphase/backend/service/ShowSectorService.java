package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;

import java.util.List;
import java.util.Optional;

public interface ShowSectorService {

    /**
     * Creates a new ShowSector.
     *
     * @param showSector the ShowSector to create
     * @return the created ShowSector
     */
    ShowSector createShowSector(ShowSector showSector);

    /**
     * Retrieves a ShowSector by Show ID and Sector ID.
     *
     * @param showId   the ID of the Show
     * @param sectorId the ID of the Sector
     * @return an Optional containing the ShowSector if found
     */
    Optional<ShowSector> getShowSectorBySectorId(Long showId, Long sectorId);


    /**
     * Creates multiple ShowSector entities for a given Show.
     *
     * @param show the show for which to create the show sectors
     * @return the list of created ShowSector entities
     */
    List<ShowSector> createShowSectors(Show show);
}