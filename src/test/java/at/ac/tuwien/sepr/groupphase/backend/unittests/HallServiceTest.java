package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;

import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleHallService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

@SpringBootTest
@ActiveProfiles("test")
class HallServiceTest {

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private SimpleHallService hallService;

    @Autowired
    private HallMapper hallMapper;

    private static Hall testHall;

    @BeforeAll
    static void beforeAll(@Autowired HallRepository hallRepository) {
        // Create and save test hall
        Hall hall = new Hall();
        hall.setName("Test Hall");
        hall.setCapacity(1000);
        hall.setCanvasWidth(1000);
        hall.setCanvasHeight(800);

        Stage stage = new Stage();
        stage.setPositionX(100);
        stage.setPositionY(50);
        stage.setWidth(200);
        stage.setHeight(100);
        hall.setStage(stage);

        Sector sector = new Sector();
        sector.setSectorName("Sector 1");
        sector.setColumns(10);
        sector.setRows(10);
        sector.setPrice(50L);
        hall.addSector(sector);

        testHall = hallRepository.save(hall);
    }

    @AfterAll
    static void afterAll(@Autowired HallRepository hallRepository) {
        hallRepository.deleteAll();
    }

    @Test
    void givenExistingId_whenFindOne_thenReturnHall() {
        // Act
        Hall found = hallService.findOne(testHall.getId());

        // Assert
        assertNotNull(found);
        assertEquals(testHall.getId(), found.getId());
        assertEquals(1000, found.getCanvasWidth());
        assertEquals(800, found.getCanvasHeight());
    }

    @Test
    void whenFindAll_thenReturnAllHalls() {
        // Act
        List<Hall> halls = hallService.findAll();

        // Assert
        assertFalse(halls.isEmpty());
        assertTrue(halls.stream().anyMatch(h -> h.getId().equals(testHall.getId())));
    }

    @Test
    void givenExistingId_whenDelete_thenSuccess() {
        // Arrange
        Hall newHall = new Hall();
        newHall.setCanvasWidth(800);
        newHall.setCanvasHeight(600);
        newHall.setName("Test Hall");
        newHall.setCapacity(1000);  
        Hall savedHall = hallRepository.save(newHall);

        // Act & Assert
        assertDoesNotThrow(() -> hallService.deleteHall(savedHall.getId()));
        assertFalse(hallRepository.existsById(savedHall.getId()));
    }

    @Test
    void givenNonExistingId_whenDelete_thenThrowNotFoundException() {
        // Act & Assert
        assertThrows(NotFoundException.class, () -> hallService.deleteHall(999L));
    }

    @Test
    void whenCreateHall_thenHallIsSavedAndReturned() {
        // Arrange
        Hall newHall = new Hall();
        newHall.setCanvasWidth(1200);
        newHall.setCanvasHeight(900);
        newHall.setName("Test Hall");
        newHall.setCapacity(1000);

        Stage stage = new Stage();
        stage.setPositionX(150);
        stage.setPositionY(75);
        stage.setWidth(250);
        stage.setHeight(150);
        newHall.setStage(stage);

        Sector sector = new Sector();
        sector.setSectorName("2");
        sector.setColumns(15);
        sector.setRows(15);
        sector.setPrice(75L);
        newHall.addSector(sector);

        // Convert Hall to HallDto
        HallDto hallDto = hallMapper.hallToHallDto(newHall);

        // Act
        Hall createdHallDto = hallService.createHall(hallDto);

        // Assert
        assertNotNull(createdHallDto);
        assertNotNull(createdHallDto.getId());
        assertEquals(1200, createdHallDto.getCanvasWidth());
        assertEquals(900, createdHallDto.getCanvasHeight());
        
        // Compare Stage properties individually
        assertEquals(150, createdHallDto.getStage().getPositionX());
        assertEquals(75, createdHallDto.getStage().getPositionY());
        assertEquals(250, createdHallDto.getStage().getWidth());
        assertEquals(150, createdHallDto.getStage().getHeight());
        
        // Compare first sector properties
        Sector createdSector = createdHallDto.getSectors().iterator().next();
        assertEquals("2", createdSector.getSectorName());
        assertEquals(15, createdSector.getColumns());
        assertEquals(15, createdSector.getRows());
        assertEquals(75L, createdSector.getPrice());
    }
} 