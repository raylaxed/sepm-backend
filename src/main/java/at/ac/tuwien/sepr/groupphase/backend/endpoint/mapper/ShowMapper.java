package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleShowDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper interface for converting between Show entities and DTOs.
 * This mapper provides methods to convert Show entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 * Uses other mappers for handling related entities like artists, venues, halls, tickets, and show sectors.
 */
@Mapper(uses = {ArtistMapper.class, VenueMapper.class, HallMapper.class, TicketMapper.class, ShowSectorMapper.class})
public interface ShowMapper {

    /**
     * Converts a Show entity to a SimpleShowDto.
     * This method maps only the basic properties of a show.
     *
     * @param show the Show entity to convert
     * @return the corresponding SimpleShowDto
     */
    @Named("simpleShow")
    SimpleShowDto showToSimpleShowDto(Show show);

    /**
     * Converts a list of Show entities to a list of SimpleShowDtos.
     * This method uses the named mapping "simpleShow" since SimpleShowDto misses the text property.
     *
     * @param show the list of Show entities to convert
     * @return the corresponding list of SimpleShowDtos
     */
    @IterableMapping(qualifiedByName = "simpleShow")
    List<SimpleShowDto> showToSimpleShowDto(List<Show> show);

    /**
     * Converts a Show entity to a DetailedShowDto.
     * This method includes all show properties and maps related entities such as
     * artists, venue, hall, tickets, and show sectors.
     *
     * @param show the Show entity to convert
     * @return the corresponding DetailedShowDto
     */
    @Mapping(source = "artists", target = "artists")
    @Mapping(source = "venue", target = "venue")
    @Mapping(source = "hall", target = "hall")
    @Mapping(source = "tickets", target = "tickets")
    @Mapping(source = "showSectors", target = "showSectors")
    DetailedShowDto showToDetailedShowDto(Show show);

    /**
     * Converts a ShowInquiryDto to a Show entity.
     * This method maps the show sectors from the inquiry DTO to the Show entity.
     *
     * @param showInquiryDto the ShowInquiryDto to convert
     * @return the corresponding Show entity
     */
    @Mapping(source = "showSectors", target = "showSectors")
    Show showInquiryDtoToShow(ShowInquiryDto showInquiryDto);
}


