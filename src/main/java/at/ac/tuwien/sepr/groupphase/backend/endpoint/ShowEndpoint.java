package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/v1/shows")
public class ShowEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShowService showService;
    private final ShowMapper showMapper;

    @Autowired
    public ShowEndpoint(ShowService showService, ShowMapper showMapper) {
        this.showService = showService;
        this.showMapper = showMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of shows without details", security = @SecurityRequirement(name = "apiKey"))
    @Transactional(readOnly = true)
    public List<SimpleShowDto> findAll() {
        LOGGER.info("GET /api/v1/shows");
        return showMapper.showToSimpleShowDto(showService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/filter")
    @Operation(summary = "Get all shows matching the filter", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleShowDto> findAllWithFilter(SearchShowDto searchShowDto) {
        LOGGER.info("GET /api/v1/shows with filter - name: {}, minPrice: {}, maxPrice: {}, date: {}, timeFrom: {}, timeTo: {}, eventName: {}, venueId: {}, type: {}",
            searchShowDto.getName(),
            searchShowDto.getMinPrice(),
            searchShowDto.getMaxPrice(),
            searchShowDto.getDate(),
            searchShowDto.getTimeFrom(),
            searchShowDto.getTimeTo(),
            searchShowDto.getEventName(),
            searchShowDto.getVenueId(),
            searchShowDto.getType());
        return showMapper.showToSimpleShowDto(showService.showsByFilter(searchShowDto));
    }

    @Secured("ROLE_USER")
    @GetMapping("/available")
    @Operation(summary = "Get list of shows without event", security = @SecurityRequirement(name = "apiKey"))
    @Transactional(readOnly = true)
    public List<SimpleShowDto> getShowsWithoutEvent(
        @RequestParam(name = "search", required = false) String searchQuery,
        @RequestParam(name = "durationFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate durationFrom,
        @RequestParam(name = "durationTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate durationTo) {
        LOGGER.info("GET /api/v1/shows/without-event searchQuery={}, dateFrom={}, dateTo={}", searchQuery, durationFrom, durationTo);
        return showMapper.showToSimpleShowDto(showService.findShowsWithoutEvent(searchQuery, durationFrom, durationTo));

    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific show", security = @SecurityRequirement(name = "apiKey"))
    public DetailedShowDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/shows/{}", id);
        return showMapper.showToDetailedShowDto(showService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Create a new show", security = @SecurityRequirement(name = "apiKey"))
    public DetailedShowDto create(@Valid @RequestPart("show") ShowInquiryDto showDto,
                                   @Valid @RequestPart(value = "image", required = false) MultipartFile image) throws ConflictException {
        List<ShowSectorDto> showSectors = showDto.getShowSectors();
        LOGGER.info("POST /api/v1/shows body: {}", showDto);
        LOGGER.info("Show sectors: {}", showSectors);

        String imageUrl;
        if (image != null && !image.isEmpty()) {
            imageUrl = showService.saveImage(image);
            showDto.setImageUrl(imageUrl);
        }

        // Set min and max prices from show sectors
        if (showSectors != null && !showSectors.isEmpty()) {
            double minPrice = showSectors.stream()
                .mapToDouble(ShowSectorDto::getPrice)
                .min()
                .orElse(0.0);
            double maxPrice = showSectors.stream()
                .mapToDouble(ShowSectorDto::getPrice)
                .max()
                .orElse(0.0);
            showDto.setMinPrice(minPrice);
            showDto.setMaxPrice(maxPrice);
        }

        Show show = showMapper.showInquiryDtoToShow(showDto);
        Show createdShow = showService.createShow(show);

        return showMapper.showToDetailedShowDto(createdShow);
    }

    @Secured("ROLE_USER")
    @GetMapping("/tickets/shows")
    @Operation(summary = "Get shows by IDs", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleShowDto> getShowsByIds(@RequestParam("ids") List<Long> showIds) {
        LOGGER.info("GET /api/v1/shows/tickets/shows?ids={}", showIds);
        return showMapper.showToSimpleShowDto(showService.findAllByIds(showIds));
    }

    @Secured("ROLE_USER")
    @GetMapping("/hall/{hallId}")
    @Operation(summary = "Get all shows for a specific hall")
    public List<SimpleShowDto> getShowsByHallId(@PathVariable Long hallId) {
        LOGGER.info("GET /api/v1/shows/hall/{}", hallId);
        return showMapper.showToSimpleShowDto(showService.findShowsByHallId(hallId));
    }
}
