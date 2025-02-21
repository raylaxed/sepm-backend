package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.StandingSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.HallService;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StandingSectorDto;

import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@Transactional
public class SimpleHallService implements HallService {
    private final HallRepository hallRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private StandingSectorRepository standingSectorRepository;

    @Autowired
    private SeatRepository seatRepository;

    public SimpleHallService(HallRepository hallRepository, HallMapper hallMapper,
                             SeatRepository seatRepository, SectorRepository sectorRepository, StandingSectorRepository standingSectorRepository) {
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
        this.sectorRepository = sectorRepository;
        this.standingSectorRepository = standingSectorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> findAll() {
        return hallRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Hall findOne(Long id) {
        LOGGER.info("Service Layer: Finding hall with id: {}", id);
        return hallRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Could not find hall with id " + id));
    }

    @Override
    public Hall createHall(HallDto hallDto) {
        LOGGER.info("Service Layer: Creating hall with name={}, capacity={}, width={}, height={}, stage={}, sectors={}",
            hallDto.name(), hallDto.capacity(), hallDto.canvasWidth(), hallDto.canvasHeight(),
            hallDto.stage(), hallDto.sectors());

        Hall hall = new Hall();
        hall.setName(hallDto.name());
        hall.setCapacity(hallDto.capacity());
        hall.setCanvasWidth(hallDto.canvasWidth());
        hall.setCanvasHeight(hallDto.canvasHeight());

        if (hallDto.stage() != null) {
            Stage stage = new Stage();
            stage.setPositionX(hallDto.stage().positionX());
            stage.setPositionY(hallDto.stage().positionY());
            stage.setWidth(hallDto.stage().width());
            stage.setHeight(hallDto.stage().height());
            hall.setStage(stage);
        }

        if (hallDto.sectors() != null) {
            for (SectorDto sectorDto : hallDto.sectors()) {
                Sector sector = new Sector();
                sector.setSectorName(sectorDto.sectorName().toString());
                sector.setRows(sectorDto.rows());
                sector.setColumns(sectorDto.columns());
                sector.setPrice(sectorDto.price());

                if (sectorDto.seats() != null) {
                    for (SeatDto seatDto : sectorDto.seats()) {
                        Seat seat = new Seat();
                        seat.setRowSeat(seatDto.rowSeat());
                        seat.setColumnSeat(seatDto.columnSeat());
                        seat.setPositionX(seatDto.positionX());
                        seat.setPositionY(seatDto.positionY());
                        seat.setSector(sector);
                        sector.getSeats().add(seat);
                    }
                }

                hall.addSector(sector);
            }
        }

        if (hallDto.standingSectors() != null) {
            for (StandingSectorDto standingSectorDto : hallDto.standingSectors()) {
                StandingSector standingSector = new StandingSector();
                standingSector.setSectorName(standingSectorDto.sectorName());
                standingSector.setCapacity(standingSectorDto.capacity());
                standingSector.setTakenCapacity(standingSectorDto.takenCapacity());
                standingSector.setPositionX1(standingSectorDto.positionX1());
                standingSector.setPositionY1(standingSectorDto.positionY1());
                standingSector.setPositionX2(standingSectorDto.positionX2());
                standingSector.setPositionY2(standingSectorDto.positionY2());
                standingSector.setPrice(standingSectorDto.price());
                hall.addStandingSector(standingSector);
            }
        }

        Hall savedHall = hallRepository.save(hall);
        LOGGER.info("Service Layer: Saved hall with id: {}", savedHall.getId());
        return savedHall;
    }

    @Override
    public Hall updateHall(HallDto hallDto) throws ValidationException {
        LOGGER.info("Service Layer: Updating hall: {}", hallDto);
        Hall existingHall = hallRepository.findById(hallDto.id())
            .orElseThrow(() -> new NotFoundException("Could not find hall with id " + hallDto.id()));

        // Remove all existing content first
        removeHallContent(hallDto.id());

        // Update basic hall properties
        existingHall.setName(hallDto.name());
        existingHall.setCapacity(hallDto.capacity());
        existingHall.setCanvasWidth(hallDto.canvasWidth());
        existingHall.setCanvasHeight(hallDto.canvasHeight());

        // Update stage
        if (hallDto.stage() != null) {
            if (existingHall.getStage() == null) {
                existingHall.setStage(new Stage());
            }
            Stage stage = existingHall.getStage();
            stage.setPositionX(hallDto.stage().positionX());
            stage.setPositionY(hallDto.stage().positionY());
            stage.setWidth(hallDto.stage().width());
            stage.setHeight(hallDto.stage().height());
        } else {
            existingHall.setStage(null);
        }

        // Clear existing sectors and their seats
        existingHall.getSectors().clear();

        // Add updated sectors and seats
        if (hallDto.sectors() != null) {
            for (SectorDto sectorDto : hallDto.sectors()) {
                Sector sector = new Sector();
                sector.setSectorName(sectorDto.sectorName().toString());
                sector.setRows(sectorDto.rows());
                sector.setColumns(sectorDto.columns());
                sector.setPrice(sectorDto.price());

                // Add seats
                if (sectorDto.seats() != null) {
                    for (SeatDto seatDto : sectorDto.seats()) {
                        Seat seat = new Seat();
                        seat.setRowSeat(seatDto.rowSeat());
                        seat.setColumnSeat(seatDto.columnSeat());
                        seat.setPositionX(seatDto.positionX());
                        seat.setPositionY(seatDto.positionY());
                        seat.setSector(sector);
                        sector.getSeats().add(seat);
                    }
                }

                existingHall.addSector(sector);
            }
        }

        // Clear existing standing sectors
        existingHall.getStandingSectors().clear();

        // Add updated standing sectors
        if (hallDto.standingSectors() != null) {
            for (StandingSectorDto standingSectorDto : hallDto.standingSectors()) {
                StandingSector standingSector = new StandingSector();
                standingSector.setSectorName(standingSectorDto.sectorName());
                standingSector.setCapacity(standingSectorDto.capacity());
                standingSector.setTakenCapacity(standingSectorDto.takenCapacity());
                standingSector.setPositionX1(standingSectorDto.positionX1());
                standingSector.setPositionY1(standingSectorDto.positionY1());
                standingSector.setPositionX2(standingSectorDto.positionX2());
                standingSector.setPositionY2(standingSectorDto.positionY2());
                standingSector.setPrice(standingSectorDto.price());
                existingHall.addStandingSector(standingSector);
            }
        }

        return hallRepository.save(existingHall);
    }

    @Override
    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new NotFoundException("Could not find hall with id " + id);
        }
        hallRepository.deleteById(id);
    }

    @Override
    public List<Seat> findSeatsById(List<Long> seatIds) {
        return seatRepository.findAllById(seatIds);
    }

    @Override
    public List<Sector> findSectorsById(List<Long> sectorIds) {
        return sectorRepository.findAllByIdWithSeats(sectorIds);
    }

    @Override
    public List<StandingSector> findStandingSectorsById(List<Long> sectorIds) {
        return standingSectorRepository.findAllById(sectorIds);
    }

    @Override
    @Transactional
    public void removeHallContent(Long hallId) throws ValidationException {
        LOGGER.debug("Remove all content for hall with id {}", hallId);

        Hall hall = findOne(hallId);

        // Check if hall has associated shows
        if (hall.getShows() != null && !hall.getShows().isEmpty()) {
            throw new ValidationException(
                "Cannot edit hall as it is associated with one or more shows",
                List.of("Hall is currently in use by one or more shows and cannot be modified")
            );
        }

        // Convert Sets to Lists
        List<Sector> sectors = new ArrayList<>(hall.getSectors());
        List<StandingSector> standingSectors = new ArrayList<>(hall.getStandingSectors());

        // Collect all seat IDs from sectors
        List<Long> seatIds = sectors.stream()
            .flatMap(sector -> sector.getSeats().stream())
            .map(Seat::getSeatId)
            .collect(Collectors.toList());

        // Delete in correct order to maintain referential integrity
        if (!seatIds.isEmpty()) {
            seatRepository.deleteAllById(seatIds);
        }

        if (!sectors.isEmpty()) {
            sectorRepository.deleteAllById(
                sectors.stream()
                    .map(Sector::getId)
                    .collect(Collectors.toList())
            );
        }

        if (!standingSectors.isEmpty()) {
            standingSectorRepository.deleteAllById(
                standingSectors.stream()
                    .map(StandingSector::getId)
                    .collect(Collectors.toList())
            );
        }
    }
}