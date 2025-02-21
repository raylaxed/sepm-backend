package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper interface for converting between Artist entities and DTOs.
 * This mapper provides methods to convert Artist entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 */
@Mapper
public interface ArtistMapper {

    /**
     * Maps an Artist entity to a SimpleArtistDto.
     *
     * @param artist the Artist entity to be mapped
     * @return the corresponding SimpleArtistDto
     */
    @Named("simpleArtist")
    SimpleArtistDto artistToSimpleArtistDto(Artist artist);

    /**
     * Maps a list of Artist entities to a list of SimpleArtistDto objects.
     *
     * @param artist the list of Artist entities to be mapped
     * @return a list of corresponding SimpleArtistDto objects
     */
    @IterableMapping(qualifiedByName = "simpleArtist")
    List<SimpleArtistDto> artistToSimpleArtistDto(List<Artist> artist);

    /**
     * Maps an Artist entity to a DetailedArtistDto.
     *
     * @param artist the Artist entity to be mapped
     * @return the corresponding DetailedArtistDto
     */
    DetailedArtistDto artistToDetailedArtistDto(Artist artist);

    /**
     * Maps a DetailedArtistDto to an Artist entity.
     *
     * @param detailedArtistDto the DetailedArtistDto to be mapped
     * @return the corresponding Artist entity
     */
    Artist detailedArtistDtoToArtist(DetailedArtistDto detailedArtistDto);

    /**
     * Maps an ArtistInquiryDto to an Artist entity.
     *
     * @param artistInquiryDto the ArtistInquiryDto to be mapped
     * @return the corresponding Artist entity
     */
    Artist artistInquiryDtoToArtist(ArtistInquiryDto artistInquiryDto);
}


