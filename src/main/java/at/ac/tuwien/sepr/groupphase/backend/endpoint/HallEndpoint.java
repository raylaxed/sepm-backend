package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StandingSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.HallService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.annotation.Secured;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

@RestController
@RequestMapping(value = "/api/v1/halls")
public class HallEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallService hallService;
    private final HallMapper hallMapper;
    private final AtomicLong sectorIdCounter = new AtomicLong(0);

    @Autowired
    public HallEndpoint(HallService hallService, HallMapper hallMapper) {
        this.hallService = hallService;
        this.hallMapper = hallMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Get all halls")
    public List<HallDto> getHalls() {
        return hallMapper.hallToHallDto(hallService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Get specific hall")
    public HallDto getHall(@PathVariable("id") Long id) {
        LOGGER.info("Endpoint Layer: GET /api/v1/halls/{}", id);
        return hallMapper.hallToHallDto(hallService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new hall")
    public HallDto createHall(@Valid @RequestBody HallInquiryDto hallInquiryDto) {
        LOGGER.info("POST /api/v1/halls body: {}", hallInquiryDto.toString());
        LOGGER.info("Creating hall with: width={}, height={}, stage={}, sectors={}",
            hallInquiryDto.getCanvasWidth(),
            hallInquiryDto.getCanvasHeight(),
            hallInquiryDto.getStage(),
            hallInquiryDto.getSectors());

        LOGGER.info("Mapped HallDto: {}", hallMapper.hallInquiryDtoToHallDto(hallInquiryDto));
        HallDto result =  hallMapper.hallToHallDto(
            hallService.createHall(hallMapper.hallInquiryDtoToHallDto(hallInquiryDto)));

        LOGGER.info("Created HallDto: {}", result);
        return result;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing hall")
    @Transactional
    public HallDto updateHall(@PathVariable("id") Long id, @Valid @RequestBody HallInquiryDto hallInquiryDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/halls/{} body: {}", id, hallInquiryDto);
        HallDto hallDto = hallMapper.hallInquiryDtoToHallDto(hallInquiryDto);

        // Create new HallDto with updated mappings
        hallDto = new HallDto(
            id,
            hallDto.name(),
            hallDto.capacity(),
            hallDto.canvasWidth(),
            hallDto.canvasHeight(),
            hallDto.stage(),
            // Map regular sectors
            hallDto.sectors() != null ? hallDto.sectors().stream()
                .map(sector -> new SectorDto(
                    sector.id() == null ? generateNewSectorId() : sector.id(),
                    sector.sectorName(),
                    sector.rows(),
                    sector.columns(),
                    sector.price(),
                    sector.seats().stream()
                        .map(seat -> new SeatDto(
                            null,
                            seat.rowSeat(), // Always set seat ID to null for update
                            seat.sector(),
                            seat.columnSeat(),
                            seat.positionX(),
                            seat.positionY()
                        ))
                        .collect(Collectors.toList())
                ))
                .collect(Collectors.toList()) : null,
            // Map standing sectors
            hallDto.standingSectors() != null ? hallDto.standingSectors().stream()
                .map(standingSector -> new StandingSectorDto(
                    standingSector.id() == null ? generateNewSectorId() : standingSector.id(),
                    standingSector.sectorName(),
                    standingSector.capacity(),
                    standingSector.takenCapacity(),
                    standingSector.positionX1(),
                    standingSector.positionY1(),
                    standingSector.positionX2(),
                    standingSector.positionY2(),
                    standingSector.price()
                ))
                .collect(Collectors.toList()) : null
        );

        return hallMapper.hallToHallDto(hallService.updateHall(hallDto));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a hall")
    public void deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("/seats")
    @Operation(summary = "Get seats by IDs")
    public List<SeatDto> getSeatsByIds(@RequestParam("seats") List<Long> seatIds) {
        LOGGER.info("GET /api/v1/halls/seats?ids={}", seatIds);
        return hallMapper.seatToSeatDto(hallService.findSeatsById(seatIds));
    }

    @Secured("ROLE_USER")
    @GetMapping("/sectors")
    @Operation(summary = "Get sectors by IDs")
    public List<SectorDto> getSectorsByIds(@RequestParam("sectors") List<Long> sectorIds) {
        LOGGER.info("GET /api/v1/halls/sectors?ids={}", sectorIds);
        return hallMapper.sectorToSectorDto(hallService.findSectorsById(sectorIds));
    }

    @Secured("ROLE_USER")
    @GetMapping("/standing-sectors")
    @Operation(summary = "Get standing sectors by IDs")
    public List<StandingSectorDto> getStandingSectorsByIds(@RequestParam("standing-sectors") List<Long> sectorIds) {
        LOGGER.info("GET /api/v1/halls/standing-sectors?ids={}", sectorIds);
        return hallMapper.standingSectorToStandingSectorDto(hallService.findStandingSectorsById(sectorIds));
    }

    private Long generateNewSectorId() {
        return sectorIdCounter.decrementAndGet();
    }

}