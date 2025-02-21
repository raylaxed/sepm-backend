package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class HallRepositoryTest {

    @Autowired
    private HallRepository hallRepository;

    private Hall createTestHall() {
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
        sector.setSectorName("Main Floor");
        sector.setColumns(10);
        sector.setRows(10);
        sector.setPrice(50L);
        hall.addSector(sector);

        return hall;
    }

    @Test
    public void givenNothing_whenSaveHall_thenFindListWithOneElementAndFindHallById() {
        Hall hall = createTestHall();
        Hall savedHall = hallRepository.save(hall);

        assertAll(
            () -> assertEquals(1, hallRepository.findAll().size()),
            () -> assertTrue(hallRepository.findById(savedHall.getId()).isPresent())
        );
    }

    @Test
    public void givenHall_whenDelete_thenHallIsRemoved() {
        Hall hall = createTestHall();
        Hall savedHall = hallRepository.save(hall);
        Long hallId = savedHall.getId();

        hallRepository.deleteById(hallId);

        assertAll(
            () -> assertEquals(0, hallRepository.findAll().size()),
            () -> assertFalse(hallRepository.findById(hallId).isPresent())
        );
    }

    @Test
    public void givenMultipleHalls_whenFindAll_thenReturnAllHalls() {
        Hall hall1 = createTestHall();
        Hall hall2 = new Hall();
        hall2.setName("Test Hall 2");
        hall2.setCapacity(1200);
        hall2.setCanvasWidth(800);
        hall2.setCanvasHeight(600);

        Stage stage2 = new Stage();
        stage2.setPositionX(80);
        stage2.setPositionY(40);
        stage2.setWidth(150);
        stage2.setHeight(80);
        hall2.setStage(stage2);

        hallRepository.save(hall1);
        hallRepository.save(hall2);

        assertEquals(2, hallRepository.findAll().size());
    }

    @Test
    public void givenHall_whenFindById_thenReturnCorrectHall() {
        Hall hall = createTestHall();
        Hall savedHall = hallRepository.save(hall);

        Hall foundHall = hallRepository.findById(savedHall.getId()).orElse(null);

        assertAll(
            () -> assertNotNull(foundHall),
            () -> assertEquals(1000, foundHall.getCanvasWidth()),
            () -> assertEquals(800, foundHall.getCanvasHeight()),
            () -> assertNotNull(foundHall.getStage()),
            () -> assertEquals(100, foundHall.getStage().getPositionX()),
            () -> assertEquals(1, foundHall.getSectors().size()),
            () -> {
                Sector sector = foundHall.getSectors().iterator().next();
                assertEquals("Main Floor", sector.getSectorName());
            }
        );
    }
} 