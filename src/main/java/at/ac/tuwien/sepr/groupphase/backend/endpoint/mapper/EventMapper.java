package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper interface for converting between Event entities and DTOs.
 * This mapper provides methods to convert Event entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 */
@Mapper
public interface EventMapper {

    /**
     * Maps an Event entity to a SimpleEventDto.
     *
     * @param event the Event entity to be mapped
     * @return the corresponding SimpleEventDto
     */
    @Named("simpleEvent")
    SimpleEventDto eventToSimpleEventDto(Event event);

    /**
     * Maps a list of Event entities to a list of SimpleEventDto objects.
     * This is necessary because SimpleEventDto does not include the detailed text and duration properties.
     *
     * @param events the list of Event entities to be mapped
     * @return a list of corresponding SimpleEventDto objects
     */
    @IterableMapping(qualifiedByName = "simpleEvent")
    List<SimpleEventDto> eventToSimpleEventDto(List<Event> events);

    /**
     * Maps an Event entity to a DetailedEventDto, including show information.
     *
     * @param event the Event entity to be mapped
     * @return the corresponding DetailedEventDto
     */
    @Mapping(source = "shows", target = "shows")
    DetailedEventDto eventToDetailedEventDto(Event event);

    /**
     * Maps an EventInquiryDto to an Event entity.
     *
     * @param eventInquiryDto the EventInquiryDto to be mapped
     * @return the corresponding Event entity
     */
    Event eventInquiryDtoToEvent(EventInquiryDto eventInquiryDto);

    /**
     * Maps an Event entity to an EventInquiryDto.
     *
     * @param event the Event entity to be mapped
     * @return the corresponding EventInquiryDto
     */
    EventInquiryDto eventToEventInquiryDto(Event event);
}
