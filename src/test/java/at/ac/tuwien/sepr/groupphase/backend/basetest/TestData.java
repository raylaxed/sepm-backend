package at.ac.tuwien.sepr.groupphase.backend.basetest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestNewsText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String NEWS_BASE_URI = BASE_URI + "/news";

    String TEST_EVENT_NAME = "Name";
    String TEST_EVENT_SUMMARY = "Summary";
    String TEST_EVENT_TEXT = "TestEventText";
    int TEST_EVENT_DURATION = 50;
    String TEST_EVENT_TYPE = "Type";
    int TEST_EVENT_SOLD_SEATS = 10;
    String TEST_EVENT_IMAGE = "/images/test_image.png";
    LocalDate TEST_EVENT_DURATION_FROM = LocalDate.now();
    LocalDate TEST_EVENT_DURATION_TO = LocalDate.now().plusDays(7);
    String EVENT_BASE_URI = BASE_URI + "/events";

    String TEST_SHOW_NAME = "Test Show";
    String TEST_SHOW_SUMMARY = "This is a summary of the show";
    String TEST_SHOW_TEXT = "This is a detailed description of the show";
    String TEST_SHOW_TYPE = "Concert";
    String TEST_SHOW_IMAGE = TEST_EVENT_IMAGE;
    int TEST_SHOW_DURATION = 120;
    LocalDate TEST_SHOW_DATE = LocalDate.of(2025, 10, 1);
    LocalTime TEST_SHOW_TIME = LocalTime.of(19, 0);
    int TEST_SHOW_CAPACITY = 100;
    int TEST_SHOW_SOLD_SEATS = 50;
    String SHOW_BASE_URI = BASE_URI + "/shows";

    String TEST_ARTIST_NAME = "Test Artist";
    String TEST_ARTIST_SUMMARY = "This is a summary for artist";
    String TEST_ARTIST_TEXT = "This is a detailed description for artist";
    String TEST_ARTIST_IMAGE = TEST_EVENT_IMAGE;
    String ARTIST_BASE_URI = BASE_URI + "/artists";


    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    String TEST_USER_FIRSTNAME = "John";
    String TEST_USER_LASTNAME = "Doe";
    String TEST_USER_EMAIL = "john.doe@example.com";
    String TEST_USER_PASSWORD = "password123";
    String TEST_USER_ADDRESS = "1 Test Street";
    Date TEST_USER_DOB = java.sql.Date.valueOf(LocalDate.of(1990, 1, 1));

    String TEST_INVALID_EMAIL = "not-an-email";
    String TEST_EXISTING_EMAIL = "existing@example.com";


    // Venue test data
    String TEST_VENUE_NAME = "Test Venue";
    String TEST_VENUE_STREET = "Teststreet 1";
    String TEST_VENUE_CITY = "Vienna";
    String TEST_VENUE_COUNTY = "Vienna";
    String TEST_VENUE_POSTAL_CODE = "1010";
    List<Long> TEST_VENUE_HALL_IDS = List.of(1L);

    // Hall test data
    Integer TEST_HALL_CANVAS_WIDTH = 1000;
    Integer TEST_HALL_CANVAS_HEIGHT = 800;
    Integer TEST_HALL_CAPACITY = 1000;
    String TEST_HALL_NAME = "Test Hall";

    // Stage test data
    Integer TEST_STAGE_POSITION_X = 100;
    Integer TEST_STAGE_POSITION_Y = 50;
    Integer TEST_STAGE_WIDTH = 200;
    Integer TEST_STAGE_HEIGHT = 100;

    // Sector test data
    String TEST_SECTOR_NAME = "Main Floor";
    Integer TEST_SECTOR_ROWS = 10;
    Integer TEST_SECTOR_COLUMNS = 10;
    Long TEST_SECTOR_PRICE = 50L;
    Double TEST_SECTOR_PRICE_DOUBLE = 4.5;

    // Seat test data
    Integer TEST_SEAT_ROW = 1;
    Integer TEST_SEAT_COLUMN = 1;
    Integer TEST_SEAT_POSITION_X = 10;
    Integer TEST_SEAT_POSITION_Y = 10;

    // Ticket test data
    Long TEST_TICKET_ID = 1L;
    LocalDateTime TEST_TICKET_PURCHASE_DATE = LocalDateTime.now();
    String TEST_TICKET_CANCELLATION_CODE = "ABC123XYZ";
    Double TEST_TICKET_PRICE = 49.99;

    // Order test data
    Long TEST_ORDER_ID = 1L;
    String TEST_CARD_HOLDER = "John Doe";
    String TEST_CARD_NUMBER = "************1234";
    String TEST_EXPIRY_DATE = "12/25";
    LocalDateTime TEST_ORDER_DATE = LocalDateTime.now();

    // Helper method to create a test venue with hall
    static Venue createTestVenueWithHall() {
        // Create Stage
        Stage stage = new Stage();
        stage.setPositionX(TEST_STAGE_POSITION_X);
        stage.setPositionY(TEST_STAGE_POSITION_Y);
        stage.setWidth(TEST_STAGE_WIDTH);
        stage.setHeight(TEST_STAGE_HEIGHT);

        // Create Seat
        Seat seat = new Seat();
        seat.setRowSeat(TEST_SEAT_ROW);
        seat.setColumnSeat(TEST_SEAT_COLUMN);
        seat.setPositionX(TEST_SEAT_POSITION_X);
        seat.setPositionY(TEST_SEAT_POSITION_Y);

        // Create Sector
        Sector sector = new Sector();
        sector.setSectorName(TEST_SECTOR_NAME);
        sector.setRows(TEST_SECTOR_ROWS);
        sector.setColumns(TEST_SECTOR_COLUMNS);
        sector.setPrice(TEST_SECTOR_PRICE);
        sector.getSeats().add(seat);
        seat.setSector(sector);

        // Create Hall
        Hall hall = new Hall();
        hall.setCanvasWidth(TEST_HALL_CANVAS_WIDTH);
        hall.setCanvasHeight(TEST_HALL_CANVAS_HEIGHT);
        hall.setStage(stage);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        hall.setSectors(sectors);

        // Create Venue
        Venue venue = new Venue();
        venue.setName(TEST_VENUE_NAME);
        venue.setStreet(TEST_VENUE_STREET);
        venue.setCity(TEST_VENUE_CITY);
        venue.setCounty(TEST_VENUE_COUNTY);
        venue.setPostalCode(TEST_VENUE_POSTAL_CODE);
        venue.setHallIds(TEST_VENUE_HALL_IDS);

        return venue;
    }

    // Helper method to create a test hall
    static Hall createTestHall() {
        Stage stage = new Stage();
        stage.setPositionX(TEST_STAGE_POSITION_X);
        stage.setPositionY(TEST_STAGE_POSITION_Y);
        stage.setWidth(TEST_STAGE_WIDTH);
        stage.setHeight(TEST_STAGE_HEIGHT);

        Seat seat = new Seat();
        seat.setRowSeat(TEST_SEAT_ROW);
        seat.setColumnSeat(TEST_SEAT_COLUMN);
        seat.setPositionX(TEST_SEAT_POSITION_X);
        seat.setPositionY(TEST_SEAT_POSITION_Y);

        Sector sector = new Sector();
        sector.setSectorName(TEST_SECTOR_NAME);
        sector.setRows(TEST_SECTOR_ROWS);
        sector.setColumns(TEST_SECTOR_COLUMNS);
        sector.setPrice(TEST_SECTOR_PRICE);
        sector.getSeats().add(seat);
        seat.setSector(sector);

        Hall hall = new Hall();
        hall.setCanvasWidth(TEST_HALL_CANVAS_WIDTH);
        hall.setCanvasHeight(TEST_HALL_CANVAS_HEIGHT);
        hall.setStage(stage);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        hall.setSectors(sectors);
        hall.setCapacity(TEST_HALL_CAPACITY);
        hall.setName(TEST_HALL_NAME);

        return hall;
    }

    // Helper method to create a test ticket
    public static Ticket createTestTicket() {
        // Create user
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(TEST_USER_FIRSTNAME);
        user.setLastName(TEST_USER_LASTNAME);
        user.setEmail(TEST_USER_EMAIL);
        user.setAddress(TEST_USER_ADDRESS);
        user.setDateOfBirth(TEST_USER_DOB);

        // Create venue with hall
        Venue venue = createTestVenueWithHall();
        Hall hall = createTestHall();
        hall.setVenue(venue);
        Set<Hall> halls = new HashSet<>();
        halls.add(hall);

        // Create show and connect it to venue and hall
        Show show = new Show();
        show.setName(TEST_SHOW_NAME);
        show.setDate(TEST_SHOW_DATE);
        show.setTime(TEST_SHOW_TIME);
        show.setVenue(venue);
        show.setHall(hall);

        // Create sector and seat
        Sector sector = new Sector();
        sector.setSectorName(TEST_SECTOR_NAME);
        sector.setRows(TEST_SECTOR_ROWS);
        sector.setColumns(TEST_SECTOR_COLUMNS);
        sector.setPrice(TEST_SECTOR_PRICE);
        sector.setHall(hall);

        Seat seat = new Seat();
        seat.setRowSeat(TEST_SEAT_ROW);
        seat.setColumnSeat(TEST_SEAT_COLUMN);
        seat.setPositionX(TEST_SEAT_POSITION_X);
        seat.setPositionY(TEST_SEAT_POSITION_Y);
        seat.setSector(sector);

        // Create and return ticket
        return Ticket.TicketBuilder.aTicket()
            .withId(TEST_TICKET_ID)
            .withTicketType("Regular")
            .withPrice(TEST_TICKET_PRICE)
            .withUser(user)
            .withShow(show)
            .withSeat(seat)
            .withPurchased(true)
            .build();
    }

    // Helper method to create a test order
    static Order createTestOrder() {
        Ticket ticket = createTestTicket();
        return Order.OrderBuilder.anOrder()
            .withId(TEST_ORDER_ID)
            .withTotal(TEST_TICKET_PRICE)
            .withOrderDate(TEST_ORDER_DATE)
            .withTickets(List.of(ticket))
            .withUser(ticket.getUser())
            .withPaymentIntentId("pi_test_" + TEST_ORDER_ID)
            .build();
    }
}
