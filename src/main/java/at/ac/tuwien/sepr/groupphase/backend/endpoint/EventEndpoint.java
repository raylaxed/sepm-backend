package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



import java.lang.invoke.MethodHandles;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/events")
@Validated
public class EventEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventMapper eventMapper;
    private final EventService eventService;

    @Autowired
    public EventEndpoint(EventMapper eventMapper, EventService eventService) {
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }


    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of events without details", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleEventDto> findAll() {
        LOGGER.info("GET /api/v1/events");
        return eventMapper.eventToSimpleEventDto(eventService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/filter")
    @Operation(summary = "Get all the events matching the filter", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleEventDto> findAllWithFilter(SearchEventDto searchEventDto) {
        LOGGER.info("GET /api/v1/events with filter {}", searchEventDto);
        return eventMapper.eventToSimpleEventDto(eventService.eventsByFilter(searchEventDto));
    }


    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Transactional
    @Operation(summary = "Get detailed information about a specific event", security = @SecurityRequirement(name = "apiKey"))
    public DetailedEventDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/events/{}", id);
        return eventMapper.eventToDetailedEventDto(eventService.findOne(id));
    }


    @Secured("ROLE_USER")
    @GetMapping("/top-ten")
    @Operation(summary = "Get list of top 10 events without details by sold seats", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleEventDto> findTop10(@RequestParam(value = "type", required = false) String eventType) {
        LOGGER.info("GET /api/v1/events/top-ten with type {}", eventType);
        return eventMapper.eventToSimpleEventDto(eventService.findTop10BySoldSeats(eventType));
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Publish a new event", security = @SecurityRequirement(name = "apiKey"))
    public DetailedEventDto create(@Valid @RequestPart("event") EventInquiryDto event,
                                   @Valid @RequestPart(value = "image", required = false) MultipartFile image) {
        String imageUrl;
        if (image != null && !image.isEmpty()) {
            imageUrl = eventService.saveImage(image);
            event.setImageUrl(imageUrl);
        }
        LOGGER.info("POST /api/v1/event body: {}", event);
        return eventMapper.eventToDetailedEventDto(
            eventService.createEvent(eventMapper.eventInquiryDtoToEvent(event)));
    }
}
