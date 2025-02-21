package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchVenueDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.VenueMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.VenueService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

import jakarta.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/venues")
public class VenueEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final VenueService venueService;
    private final VenueMapper venueMapper;
    private final HallMapper hallMapper;

    @Autowired
    public VenueEndpoint(VenueService venueService, VenueMapper venueMapper, HallMapper hallMapper) {
        this.venueService = venueService;
        this.venueMapper = venueMapper;
        this.hallMapper = hallMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Get all venues")
    public List<VenueDto> getVenues() {
        LOGGER.info("GET /api/v1/venues");
        return venueMapper.venueToVenueDto(venueService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Get specific venue")
    public VenueDto getVenue(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/venues/{}", id);
        return venueMapper.venueToVenueDto(venueService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new venue")
    public VenueDto createVenue(@Valid @RequestBody VenueDto venueDto) throws ValidationException, ConflictException {
        LOGGER.info("POST /api/v1/venues body: {}", venueDto);
        VenueDto createdVenueDto = venueMapper.venueToVenueDto(venueService.createVenue(venueDto));
        LOGGER.info("Endpointlayer: created venue hall id: {}", createdVenueDto.hallIds());
        return createdVenueDto;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing venue")
    @Transactional
    public VenueDto updateVenue(
        @PathVariable("id") Long id,
        @Valid @RequestBody VenueDto venueDto
    ) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.info("PUT /api/v1/venues/{} body: {}", id, venueDto);
        return venueMapper.venueToVenueDto(venueService.updateVenue(id, venueDto));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a venue")
    public void deleteVenue(@PathVariable("id") Long id) throws NotFoundException, ValidationException {
        LOGGER.info("DELETE /api/v1/venues/{}", id);
        venueService.deleteVenue(id);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search")
    @Transactional(readOnly = true)
    @Operation(summary = "Search for venues by name", security = @SecurityRequirement(name = "apiKey"))
    public List<VenueDto> searchVenues(@RequestParam(name = "search") String query) {
        LOGGER.info("GET /api/v1/venues/search?search={}", query);
        return venueMapper.venueToVenueDto(venueService.searchVenuesByName(query));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}/halls")
    @Transactional(readOnly = true)
    @Operation(summary = "Get all halls for a specific venue")
    public List<HallDto> getHallsByVenueId(@PathVariable("id") Long id) {
        LOGGER.info("GET /api/v1/venues/{}/halls", id);
        return hallMapper.hallToHallDto(venueService.getHallsByVenueId(id));
    }

    @Secured("ROLE_USER")
    @GetMapping("/filter")
    @Operation(summary = "Filter venues based on search criteria")
    public List<VenueDto> filterVenues(SearchVenueDto searchDto) {
        LOGGER.info("GET /api/v1/venues/filter with searchDto: {}", searchDto);
        return venueMapper.venueToVenueDto(venueService.filterVenues(searchDto));
    }

    @Secured("ROLE_USER")
    @GetMapping("/countriesAndCities")
    @Operation(summary = "Loads a String[] of unique counties and a String[] of unique cities that are already in the db")
    public List<String[]> availableCountriesAndCities() {
        LOGGER.info("GET /api/v1/countriesAndCities");
        return venueService.findUniqueCountriesAndCities();
    }
}