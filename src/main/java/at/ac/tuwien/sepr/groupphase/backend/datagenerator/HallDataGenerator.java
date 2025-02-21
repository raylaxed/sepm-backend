package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
@Order(1)
public class HallDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallRepository hallRepository;

    public HallDataGenerator(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @PostConstruct
    private void generateHall() {
        if (hallRepository.findAll().size() > 0) {
            LOGGER.debug("halls already generated");
            return;
        }

        LOGGER.debug("generating halls");

        // Generate 25 halls (to have enough for 10 venues with 2-3 halls each)
        for (int i = 1; i <= 25; i++) {
            Hall hall;
            // Rotate between the three hall types
            switch (i % 3) {
                case 0:
                    hall = createArena("Arena " + i);
                    break;
                case 1:
                    hall = createMainHall("Concert Hall " + i);
                    break;
                case 2:
                    hall = createSmallTheater("Theater " + i);
                    break;
                default:
                    hall = createMainHall("Hall " + i);
            }
            LOGGER.debug("saving hall{} {}", i, hall);
            hallRepository.save(hall);
        }
    }

    private Hall createMainHall(String name) {
        Hall hall = new Hall();
        hall.setName(name);
        hall.setCanvasWidth(1000);
        hall.setCanvasHeight(800);

        Stage stage = new Stage();
        stage.setPositionX(400);
        stage.setPositionY(50);
        stage.setWidth(200);
        stage.setHeight(100);
        hall.setStage(stage);

        Sector frontSector = new Sector();
        frontSector.setSectorName("1");
        frontSector.setColumns(10);
        frontSector.setRows(5);
        frontSector.setPrice(100L);
        generateSeats(frontSector, 200, 200);
        hall.addSector(frontSector);

        Sector backSector = new Sector();
        backSector.setSectorName("2");
        backSector.setColumns(13);
        backSector.setRows(8);
        backSector.setPrice(50L);
        generateSeats(backSector, 200, 400);
        hall.addSector(backSector);

        // Add standing sector
        StandingSector standingSector = new StandingSector();
        standingSector.setSectorName("Standing");
        standingSector.setCapacity(200);
        standingSector.setTakenCapacity(0);
        standingSector.setPositionX1(600);
        standingSector.setPositionY1(200);
        standingSector.setPositionX2(800);
        standingSector.setPositionY2(600);
        standingSector.setPrice(75L);
        hall.addStandingSector(standingSector);

        // Calculate and set total capacity
        int totalSeats = hall.getSectors().stream()
            .mapToInt(sector -> sector.getSeats().size())
            .sum();

        int totalStandingCapacity = hall.getStandingSectors().stream()
            .mapToInt(StandingSector::getCapacity)
            .sum();

        hall.setCapacity(totalSeats + totalStandingCapacity);

        return hall;
    }

    private Hall createSmallTheater(String name) {
        Hall hall = new Hall();
        hall.setName(name);
        hall.setCanvasWidth(800);
        hall.setCanvasHeight(600);

        Stage stage = new Stage();
        stage.setPositionX(300);
        stage.setPositionY(50);
        stage.setWidth(150);
        stage.setHeight(80);
        hall.setStage(stage);

        Sector vipSector = new Sector();
        vipSector.setSectorName("1");
        vipSector.setColumns(8);
        vipSector.setRows(4);
        vipSector.setPrice(150L);
        generateSeats(vipSector, 150, 200);
        hall.addSector(vipSector);

        Sector regularSector = new Sector();
        regularSector.setSectorName("2");
        regularSector.setColumns(10);
        regularSector.setRows(5);
        regularSector.setPrice(75L);
        generateSeats(regularSector, 150, 350);
        hall.addSector(regularSector);

        // Add standing sector
        StandingSector standingSector = new StandingSector();
        standingSector.setSectorName("Standing");
        standingSector.setCapacity(50);
        standingSector.setTakenCapacity(0);
        standingSector.setPositionX1(500);
        standingSector.setPositionY1(200);
        standingSector.setPositionX2(650);
        standingSector.setPositionY2(400);
        standingSector.setPrice(50L);
        hall.addStandingSector(standingSector);

        // Calculate and set total capacity
        int totalSeats = hall.getSectors().stream()
            .mapToInt(sector -> sector.getSeats().size())
            .sum();

        int totalStandingCapacity = hall.getStandingSectors().stream()
            .mapToInt(StandingSector::getCapacity)
            .sum();

        hall.setCapacity(totalSeats + totalStandingCapacity);

        return hall;
    }

    private Hall createArena(String name) {
        Hall hall = new Hall();
        hall.setName(name);
        hall.setCanvasWidth(1200);
        hall.setCanvasHeight(1000);

        Stage stage = new Stage();
        stage.setPositionX(500);
        stage.setPositionY(50);
        stage.setWidth(250);
        stage.setHeight(120);
        hall.setStage(stage);

        Sector premiumSector = new Sector();
        premiumSector.setSectorName("1");
        premiumSector.setColumns(20);
        premiumSector.setRows(8);
        premiumSector.setPrice(200L);
        generateSeats(premiumSector, 250, 200);
        hall.addSector(premiumSector);

        Sector middleSector = new Sector();
        middleSector.setSectorName("2");
        middleSector.setColumns(25);
        middleSector.setRows(6);
        middleSector.setPrice(120L);
        generateSeats(middleSector, 250, 500);
        hall.addSector(middleSector);

        Sector economySector = new Sector();
        economySector.setSectorName("3");
        economySector.setColumns(25);
        economySector.setRows(8);
        economySector.setPrice(80L);
        generateSeats(economySector, 250, 720);
        hall.addSector(economySector);

        // Add two standing sectors for the arena
        StandingSector standingSectorLeft = new StandingSector();
        standingSectorLeft.setSectorName("Standing Left");
        standingSectorLeft.setCapacity(300);
        standingSectorLeft.setTakenCapacity(0);
        standingSectorLeft.setPositionX1(50);
        standingSectorLeft.setPositionY1(200);
        standingSectorLeft.setPositionX2(200);
        standingSectorLeft.setPositionY2(800);
        standingSectorLeft.setPrice(100L);
        hall.addStandingSector(standingSectorLeft);

        StandingSector standingSectorRight = new StandingSector();
        standingSectorRight.setSectorName("Standing Right");
        standingSectorRight.setCapacity(300);
        standingSectorRight.setTakenCapacity(0);
        standingSectorRight.setPositionX1(1000);
        standingSectorRight.setPositionY1(200);
        standingSectorRight.setPositionX2(1150);
        standingSectorRight.setPositionY2(800);
        standingSectorRight.setPrice(100L);
        hall.addStandingSector(standingSectorRight);

        // Calculate and set total capacity
        int totalSeats = hall.getSectors().stream()
            .mapToInt(sector -> sector.getSeats().size())
            .sum();

        int totalStandingCapacity = hall.getStandingSectors().stream()
            .mapToInt(StandingSector::getCapacity)
            .sum();

        hall.setCapacity(totalSeats + totalStandingCapacity);

        return hall;
    }

    private void generateSeats(Sector sector, int startX, int startY) {
        int seatSpacingX = 30;
        int seatSpacingY = 30;

        for (int row = 1; row <= sector.getRows(); row++) {
            for (int col = 1; col <= sector.getColumns(); col++) {
                Seat seat = new Seat();
                seat.setRowSeat(row);
                seat.setColumnSeat(col);
                seat.setPositionX(startX + ((col - 1) * seatSpacingX));
                seat.setPositionY(startY + ((row - 1) * seatSpacingY));
                seat.setSector(sector);
                sector.getSeats().add(seat);
            }
        }
    }
}