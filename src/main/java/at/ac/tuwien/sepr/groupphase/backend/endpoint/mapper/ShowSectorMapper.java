package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting between ShowSector entities and DTOs.
 * This mapper provides methods to convert ShowSector entities to DTOs and vice versa,
 * handling the relationships between shows, sectors, and standing sectors.
 */
@Mapper(componentModel = "spring")
public interface ShowSectorMapper {

    /**
     * Converts a ShowSector entity to a ShowSectorDto.
     * Maps the IDs from the associated show, sector, and standing sector entities.
     *
     * @param showSector the ShowSector entity to convert
     * @return the corresponding ShowSectorDto
     */
    @Mapping(source = "show.id", target = "showId")
    @Mapping(source = "sector.id", target = "sectorId")
    @Mapping(source = "standingSector.id", target = "standingSectorId")
    ShowSectorDto showSectorToShowSectorDto(ShowSector showSector);

    /**
     * Converts a list of ShowSector entities to a list of ShowSectorDtos.
     *
     * @param showSectors the list of ShowSector entities to convert
     * @return the corresponding list of ShowSectorDtos
     */
    List<ShowSectorDto> showSectorsToShowSectorDtos(List<ShowSector> showSectors);

    /**
     * Converts a ShowSectorDto to a ShowSector entity.
     * Creates the show, sector, and standing sector relationships using their IDs.
     *
     * @param showSectorDto the ShowSectorDto to convert
     * @return the corresponding ShowSector entity
     */
    @Mapping(source = "showId", target = "show.id")
    @Mapping(source = "sectorId", target = "sector.id")
    @Mapping(source = "standingSectorId", target = "standingSector.id")
    ShowSector showSectorDtoToShowSector(ShowSectorDto showSectorDto);

    /**
     * Converts a list of ShowSectorDtos to a list of ShowSector entities.
     *
     * @param showSectorDtos the list of ShowSectorDtos to convert
     * @return the corresponding list of ShowSector entities
     */
    List<ShowSector> showSectorDtoToShowSector(List<ShowSectorDto> showSectorDtos);
}