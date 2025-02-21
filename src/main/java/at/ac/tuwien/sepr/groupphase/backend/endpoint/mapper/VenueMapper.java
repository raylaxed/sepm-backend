package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.ArrayList;

/**
 * Mapper interface for converting between Venue entities and DTOs.
 * This mapper provides methods to convert Venue entities to DTOs and vice versa,
 * handling the relationships between venues and halls.
 */
@Mapper
public interface VenueMapper {

    /**
     * Converts a Venue entity to a VenueDto.
     * Creates a new ArrayList for hallIds if the source is null.
     *
     * @param venue the Venue entity to convert
     * @return the corresponding VenueDto, or null if the input is null
     */
    default VenueDto venueToVenueDto(Venue venue) {
        if (venue == null) {
            return null;
        }

        return new VenueDto(
            venue.getId(),
            venue.getName(),
            venue.getStreet(),
            venue.getCity(),
            venue.getCounty(),
            venue.getPostalCode(),
            venue.getHallIds() != null ? new ArrayList<>(venue.getHallIds()) : new ArrayList<>()
        );
    }

    /**
     * Converts a list of Venue entities to a list of VenueDtos.
     *
     * @param venues the list of Venue entities to convert
     * @return the corresponding list of VenueDtos
     */
    List<VenueDto> venueToVenueDto(List<Venue> venues);

    /**
     * Converts a VenueDto to a Venue entity.
     * Maps the hallIds and ignores the showIds field during mapping.
     *
     * @param venueDto the VenueDto to convert
     * @return the corresponding Venue entity
     */
    @Mapping(source = "hallIds", target = "hallIds")
    @Mapping(target = "showIds", ignore = true)
    Venue venueDtoToVenue(VenueDto venueDto);

}