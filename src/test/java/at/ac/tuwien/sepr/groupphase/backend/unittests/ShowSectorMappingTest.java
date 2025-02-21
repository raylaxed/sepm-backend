package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShowSectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ShowSectorMappingTest {

    private ShowSectorMapper showSectorMapper;

    @BeforeEach
    void setUp() {
        showSectorMapper = Mappers.getMapper(ShowSectorMapper.class);
    }

    @Test
    void givenShowSector_whenMapToShowSectorDto_thenDtoHasAllProperties() {
        // Arrange
        Show show = new Show();
        show.setId(1L);

        Sector sector = new Sector();
        sector.setId(2L);

        StandingSector standingSector = new StandingSector();
        standingSector.setId(3L);

        ShowSector showSector = new ShowSector();
        showSector.setId(1L);
        showSector.setShow(show);
        showSector.setSector(sector);
        showSector.setStandingSector(standingSector);
        showSector.setPrice(50.0);

        // Act
        ShowSectorDto showSectorDto = showSectorMapper.showSectorToShowSectorDto(showSector);

        // Assert
        assertNotNull(showSectorDto);
        assertAll(
            () -> assertEquals(showSector.getId(), showSectorDto.getId()),
            () -> assertEquals(show.getId(), showSectorDto.getShowId()),
            () -> assertEquals(sector.getId(), showSectorDto.getSectorId()),
            () -> assertEquals(standingSector.getId(), showSectorDto.getStandingSectorId()),
            () -> assertEquals(showSector.getPrice(), showSectorDto.getPrice())
        );
    }

    @Test
    void givenShowSectorDto_whenMapToShowSector_thenEntityHasAllProperties() {
        // Arrange
        ShowSectorDto showSectorDto = new ShowSectorDto();
        showSectorDto.setId(1L);
        showSectorDto.setShowId(1L);
        showSectorDto.setSectorId(2L);
        showSectorDto.setStandingSectorId(3L);
        showSectorDto.setPrice(50.0);

        // Act
        ShowSector showSector = showSectorMapper.showSectorDtoToShowSector(showSectorDto);

        // Assert
        assertNotNull(showSector);
        assertAll(
            () -> assertEquals(showSectorDto.getId(), showSector.getId()),
            () -> assertEquals(showSectorDto.getPrice(), showSector.getPrice())
            // Additional assertions for show, sector, and standingSector can be added if needed
        );
    }
}
