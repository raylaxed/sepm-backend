package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowSectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class SimpleShowSectorService implements ShowSectorService {

    private final ShowSectorRepository showSectorRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleShowSectorService.class);

    @Autowired
    public SimpleShowSectorService(ShowSectorRepository showSectorRepository) {
        this.showSectorRepository = showSectorRepository;
    }

    @Override
    public ShowSector createShowSector(ShowSector showSector) {
        return showSectorRepository.save(showSector);
    }

    @Override
    public Optional<ShowSector> getShowSectorBySectorId(Long showId, Long sectorId) {
        return showSectorRepository.findByShowIdAndSectorId(showId, sectorId);
    }

    @Override
    @Transactional
    public List<ShowSector> createShowSectors(Show show) {
        if (show.getShowSectors() == null) {
            throw new IllegalArgumentException("ShowSectors must not be null");
        }

        Hall hall = show.getHall();
        if (hall == null) {
            throw new IllegalArgumentException("Show must have a hall");
        }

        List<ShowSector> newShowSectors = new ArrayList<>();

        for (ShowSector ss : show.getShowSectors()) {
            if (ss == null) {
                continue;
            }

            ShowSector showSector = new ShowSector();
            showSector.setShow(show);
            showSector.setPrice(ss.getPrice());

            // Handle Sector
            Long sectorId = ss.getSector() != null ? ss.getSector().getId() : null;
            Long standingSectorId = ss.getStandingSector() != null ? ss.getStandingSector().getId() : null;

            LOGGER.debug("Processing sector ID: {} and standing sector ID: {}", sectorId, standingSectorId);

            if (sectorId != null) {
                Sector matchingSector = hall.getSectors().stream()
                    .filter(s -> sectorId.equals(s.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                        String.format("Sector with ID %d not found in hall %d", sectorId, hall.getId())));
                showSector.setSector(matchingSector);
            }

            // Handle StandingSector
            if (standingSectorId != null) {
                StandingSector matchingStandingSector = hall.getStandingSectors().stream()
                    .filter(s -> standingSectorId.equals(s.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(
                        String.format("StandingSector with ID %d not found in hall %d",
                            standingSectorId, hall.getId())));
                showSector.setStandingSector(matchingStandingSector);
            }

            // Validate that either sector or standingSector is set
            if (showSector.getSector() == null && showSector.getStandingSector() == null) {
                throw new IllegalArgumentException(
                    String.format("ShowSector must have either a Sector or StandingSector. "
                        + "Received sectorId: %s, standingSectorId: %s", sectorId, standingSectorId));
            }

            newShowSectors.add(showSector);
        }

        if (newShowSectors.isEmpty()) {
            throw new IllegalArgumentException("No valid show sectors provided");
        }

        return showSectorRepository.saveAll(newShowSectors);
    }
}