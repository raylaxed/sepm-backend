package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StandingSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;

import org.mapstruct.Mapper;
import java.util.List;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Hall entities and DTOs.
 * This mapper provides methods to convert Hall entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 */
@Mapper
public interface HallMapper {

    /**
     * Converts a Hall entity to a HallDto.
     *
     * @param hall the Hall entity to convert
     * @return the corresponding HallDto
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "stage", source = "stage")
    @Mapping(target = "sectors", source = "sectors")
    @Mapping(target = "standingSectors", source = "standingSectors")
    HallDto hallToHallDto(Hall hall);

    /**
     * Converts a list of Hall entities to a list of HallDtos.
     *
     * @param halls the list of Hall entities to convert
     * @return the corresponding list of HallDtos
     */
    List<HallDto> hallToHallDto(List<Hall> halls);

    /**
     * Converts a HallInquiryDto to a HallDto, ignoring the id field.
     *
     * @param hallInquiryDto the HallInquiryDto to convert
     * @return the corresponding HallDto
     */
    @Mapping(target = "id", ignore = true)
    HallDto hallInquiryDtoToHallDto(HallInquiryDto hallInquiryDto);

    /**
     * Converts a SectorDto to a Sector entity, ignoring the id field.
     *
     * @param sectorDto the SectorDto to convert
     * @return the corresponding Sector entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sectorName", source = "sectorName")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "rows", source = "rows")
    @Mapping(target = "columns", source = "columns")
    Sector sectorDtoToSector(SectorDto sectorDto);

    /**
     * Converts a Sector entity to a SectorDto.
     *
     * @param sector the Sector entity to convert
     * @return the corresponding SectorDto
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sectorName", source = "sectorName")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "rows", source = "rows")
    @Mapping(target = "columns", source = "columns")
    SectorDto sectorToSectorDto(Sector sector);

    /**
     * Converts a list of Sector entities to a list of SectorDtos.
     *
     * @param sectors the list of Sector entities to convert
     * @return the corresponding list of SectorDtos
     */
    List<SectorDto> sectorToSectorDto(List<Sector> sectors);

    /**
     * Converts a Stage entity to a StageDto.
     *
     * @param stage the Stage entity to convert
     * @return the corresponding StageDto
     */
    StageDto stageToStageDto(Stage stage);

    /**
     * Converts a StandingSector entity to a StandingSectorDto.
     *
     * @param standingSector the StandingSector entity to convert
     * @return the corresponding StandingSectorDto
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sectorName", source = "sectorName")
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "takenCapacity", source = "takenCapacity")
    @Mapping(target = "positionX1", source = "positionX1")
    @Mapping(target = "positionY1", source = "positionY1")
    @Mapping(target = "positionX2", source = "positionX2")
    @Mapping(target = "positionY2", source = "positionY2")
    @Mapping(target = "price", source = "price")
    StandingSectorDto standingSectorToStandingSectorDto(StandingSector standingSector);

    /**
     * Converts a list of StandingSector entities to a list of StandingSectorDtos.
     *
     * @param standingSectors the list of StandingSector entities to convert
     * @return the corresponding list of StandingSectorDtos
     */
    List<StandingSectorDto> standingSectorToStandingSectorDto(List<StandingSector> standingSectors);

    /**
     * Converts a StandingSectorDto to a StandingSector entity, ignoring id and hall fields.
     *
     * @param standingSectorDto the StandingSectorDto to convert
     * @return the corresponding StandingSector entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hall", ignore = true)
    StandingSector standingSectorDtoToStandingSector(StandingSectorDto standingSectorDto);

    /**
     * Converts a Seat entity to a SeatDto.
     *
     * @param seat the Seat entity to convert
     * @return the corresponding SeatDto
     */
    @Mapping(target = "seatId", source = "seatId")
    @Mapping(target = "rowSeat", source = "rowSeat")
    @Mapping(target = "columnSeat", source = "columnSeat")
    @Mapping(target = "positionX", source = "positionX")
    @Mapping(target = "positionY", source = "positionY")
    @Mapping(target = "sector", source = "sector.id")
    SeatDto seatToSeatDto(Seat seat);

    /**
     * Converts a list of Seat entities to a list of SeatDtos.
     *
     * @param seats the list of Seat entities to convert
     * @return the corresponding list of SeatDtos
     */
    List<SeatDto> seatToSeatDto(List<Seat> seats);

    /**
     * Helper method to map a sector ID to a Sector entity.
     *
     * @param sectorId the ID of the sector
     * @return a new Sector entity with the given ID, or null if sectorId is null
     */
    default Sector map(Integer sectorId) {
        if (sectorId == null) {
            return null;
        }
        Sector sector = new Sector();
        sector.setId(Long.valueOf(sectorId));
        return sector;
    }
}