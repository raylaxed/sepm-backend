package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShowSectorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShowEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Venue testVenue;
    private Hall testHall;
    private Hall testHall2;
    private Sector sector;

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAll();
        showRepository.deleteAll();
        artistRepository.deleteAll();
        hallRepository.deleteAll();
        venueRepository.deleteAll();

        // Create and save test venue
        testVenue = new Venue();
        testVenue.setName(TEST_VENUE_NAME);
        testVenue.setStreet(TEST_VENUE_STREET);
        testVenue.setCity(TEST_VENUE_CITY);
        testVenue.setCounty(TEST_VENUE_COUNTY);
        testVenue.setPostalCode(TEST_VENUE_POSTAL_CODE);
        testVenue = venueRepository.save(testVenue);

        // Create and save test halls
        testHall = new Hall();
        testHall.setName(TEST_HALL_NAME);
        testHall.setCapacity(TEST_HALL_CAPACITY);
        testHall.setVenue(testVenue);
        testHall.setCanvasHeight(TEST_HALL_CANVAS_HEIGHT);
        testHall.setCanvasWidth(TEST_HALL_CANVAS_WIDTH);
        testHall = hallRepository.save(testHall);

        testHall2 = new Hall();
        testHall2.setName(TEST_HALL_NAME + "2");
        testHall2.setCapacity(200);
        testHall2.setVenue(testVenue);
        testHall2.setCanvasHeight(TEST_HALL_CANVAS_HEIGHT);
        testHall2.setCanvasWidth(TEST_HALL_CANVAS_WIDTH);
        testHall2 = hallRepository.save(testHall2);

        // Create and save test sector
        sector = new Sector();
        sector.setHall(testHall);
        sector.setSectorName("1");
        sector.setPrice(TEST_SECTOR_PRICE);
        sector.setRows(TEST_SECTOR_ROWS);
        sector.setColumns(TEST_SECTOR_COLUMNS);
        sector = sectorRepository.save(sector);

        // Create and save stage
        Stage stage = new Stage();
        stage.setHall(testHall);
        stage.setPositionX(TEST_STAGE_POSITION_X);
        stage.setPositionY(TEST_STAGE_POSITION_Y);
        stage.setWidth(TEST_STAGE_WIDTH);
        stage.setHeight(TEST_STAGE_HEIGHT);

        // Update venue with hall ID
        testVenue.setHallIds(List.of(testHall.getId()));
        testVenue = venueRepository.save(testVenue);
    }

    @Test
    public void givenValidData_whenPost_thenShowWithAllSetPropertiesPlusId() throws Exception {
        ShowInquiryDto showDto = new ShowInquiryDto();
        showDto.setName(TEST_SHOW_NAME);
        showDto.setSummary(TEST_SHOW_SUMMARY);
        showDto.setText(TEST_SHOW_TEXT);
        showDto.setDuration(TEST_SHOW_DURATION);
        showDto.setEventType(TEST_SHOW_TYPE);
        showDto.setDate(TEST_SHOW_DATE);
        showDto.setTime(TEST_SHOW_TIME);
        showDto.setSoldSeats(0);
        showDto.setVenueId(testVenue.getId());
        showDto.setHallId(testHall.getId());

        List<ShowSectorDto> showSectors = List.of(
            new ShowSectorDto.ShowSectorDtoBuilder()
                .withSectorId(sector.getId())
                .withPrice(TEST_SECTOR_PRICE_DOUBLE)
                .build()
        );
        showDto.setShowSectors(showSectors);

        String showJson = objectMapper.writeValueAsString(showDto);
        MockMultipartFile showPart = new MockMultipartFile(
            "show",
            "show.json",
            "application/json",
            showJson.getBytes()
        );

        ClassPathResource classPathResource = new ClassPathResource(TEST_EVENT_IMAGE);
        byte[] imageBytes = Files.readAllBytes(classPathResource.getFile().toPath());
        MockMultipartFile imagePart = new MockMultipartFile(
            "image",
            "test_image.png",
            "image/png",
            imageBytes
        );

        MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/shows")
                .file(showPart)
                .file(imagePart)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DetailedShowDto showResponse = objectMapper.readValue(response.getContentAsString(), DetailedShowDto.class);
        assertNotNull(showResponse.getId());
        assertAll(
            () -> assertEquals(TEST_SHOW_NAME, showResponse.getName()),
            () -> assertEquals(TEST_SHOW_SUMMARY, showResponse.getSummary()),
            () -> assertEquals(TEST_SHOW_TEXT, showResponse.getText()),
            () -> assertEquals(TEST_SHOW_DURATION, showResponse.getDuration()),
            () -> assertEquals(TEST_SHOW_TYPE, showResponse.getEventType()),
            () -> assertEquals(TEST_SHOW_DATE, showResponse.getDate()),
            () -> assertEquals(TEST_SHOW_TIME, showResponse.getTime()),
            () -> assertEquals(testHall.getCapacity(), showResponse.getCapacity()),
            () -> assertEquals(0, showResponse.getSoldSeats()),
            () -> assertEquals(testVenue.getId(), showResponse.getVenue().id()),
            () -> assertEquals(testHall.getId(), showResponse.getHall().id()),
            () -> assertNotNull(showResponse.getImageUrl())
        );
    }

    @Test
    public void givenInvalidData_whenPost_thenReturn422WithValidationErrors() throws Exception {
        ShowInquiryDto showDto = new ShowInquiryDto();
        // Intentionally leaving fields blank or invalid to trigger validation errors
        showDto.setName("   "); // Invalid: blank name
        showDto.setSummary(""); // Invalid: blank summary
        showDto.setText(""); // Invalid: blank description
        showDto.setDuration(0); // Invalid: duration must be positive
        showDto.setEventType(""); // Invalid: blank event type
        showDto.setSoldSeats(-1); // Invalid: negative sold seats
        showDto.setDate(null); // Invalid: date is required
        showDto.setTime(null); // Invalid: time is required
        showDto.setVenueId(testVenue.getId());
        showDto.setHallId(testHall.getId());

        String showJson = objectMapper.writeValueAsString(showDto);
        MockMultipartFile showPart = new MockMultipartFile("show", "show.json", "application/json", showJson.getBytes());

        MvcResult mvcResult = this.mockMvc.perform(multipart(SHOW_BASE_URI)
                .file(showPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // Parse the response to check for individual validation error messages
        String responseBody = response.getContentAsString();
        assertAll("Validation Error Messages",
            () -> assertTrue(responseBody.contains("Name is required")),
            () -> assertTrue(responseBody.contains("Summary is required")),
            () -> assertTrue(responseBody.contains("Description is required")),
            () -> assertTrue(responseBody.contains("Duration must be positive")),
            () -> assertTrue(responseBody.contains("Event type is required")),
            () -> assertTrue(responseBody.contains("Sold seats must not be negative")),
            () -> assertTrue(responseBody.contains("Date is required")),
            () -> assertTrue(responseBody.contains("Time is required"))
        );
    }

    @Test
    public void givenValidDataWithArtists_whenPost_thenShowWithArtists() throws Exception {
        // Create test artists
        Artist artist1 = new Artist();
        artist1.setName("Test Artist 1");
        artist1.setSummary("Test Summary 1");
        artist1.setText("Test Text 1");
        artist1 = artistRepository.save(artist1);

        Artist artist2 = new Artist();
        artist2.setName("Test Artist 2");
        artist2.setSummary("Test Summary 2");
        artist2.setText("Test Text 2");
        artist2 = artistRepository.save(artist2);

        ShowInquiryDto showDto = new ShowInquiryDto();
        showDto.setName(TEST_SHOW_NAME);
        showDto.setSummary(TEST_SHOW_SUMMARY);
        showDto.setText(TEST_SHOW_TEXT);
        showDto.setDuration(TEST_SHOW_DURATION);
        showDto.setEventType(TEST_SHOW_TYPE);
        showDto.setDate(TEST_SHOW_DATE);
        showDto.setTime(TEST_SHOW_TIME);
        showDto.setSoldSeats(TEST_SHOW_SOLD_SEATS);
        showDto.setVenueId(testVenue.getId());
        showDto.setHallId(testHall.getId());
        showDto.setArtistIds(new Long[]{artist1.getId(), artist2.getId()});

        // Create ShowSectorDto with the actual sector ID
        List<ShowSectorDto> showSectors = List.of(
            new ShowSectorDto.ShowSectorDtoBuilder()
                .withSectorId(sector.getId())
                .withPrice(50.0)
                .build()
        );
        showDto.setShowSectors(showSectors);

        String showJson = objectMapper.writeValueAsString(showDto);
        MockMultipartFile showPart = new MockMultipartFile("show", "show.json",
            "application/json", showJson.getBytes());

        ClassPathResource classPathResource = new ClassPathResource(TEST_EVENT_IMAGE);
        byte[] imageBytes = Files.readAllBytes(classPathResource.getFile().toPath());
        MockMultipartFile image = new MockMultipartFile("image", "test_image.png",
            "image/png", imageBytes);

        // Perform the POST request to create the show
        MvcResult mvcResult = this.mockMvc.perform(multipart(SHOW_BASE_URI)
                .file(showPart)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("UTF-8")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        // Verify the response
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // Verify that the show was created with the correct properties
        DetailedShowDto showResponse = objectMapper.readValue(response.getContentAsString(), DetailedShowDto.class);
        assertNotNull(showResponse.getId());
        assertEquals(TEST_SHOW_NAME, showResponse.getName());
        assertEquals(TEST_SHOW_SUMMARY, showResponse.getSummary());
        assertEquals(TEST_SHOW_TEXT, showResponse.getText());
        assertEquals(TEST_SHOW_DURATION, showResponse.getDuration());
        assertEquals(TEST_SHOW_TYPE, showResponse.getEventType());
        assertEquals(TEST_SHOW_DATE, showResponse.getDate());
        assertEquals(TEST_SHOW_TIME, showResponse.getTime());
        assertEquals(testHall.getCapacity(), showResponse.getCapacity());
        assertEquals(TEST_SHOW_SOLD_SEATS, showResponse.getSoldSeats());

        // Verify that the artists are associated with the show
        assertNotNull(showResponse.getArtists());
        assertEquals(2, showResponse.getArtists().size());
        final Long artist1Id = artist1.getId();
        final Long artist2Id = artist2.getId();
        assertTrue(showResponse.getArtists().stream().anyMatch(artist -> artist.getId().equals(artist1Id)));
        assertTrue(showResponse.getArtists().stream().anyMatch(artist -> artist.getId().equals(artist2Id)));
    }

    @Test
    public void filterShowsByName_returnsMatchingShows() throws Exception {
        // Create test shows
        Show show1 = Show.ShowBuilder.aShow()
            .withName("Rock Concert")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(150.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 1")
            .withText("Test description 1")
            .build();

        Show show2 = Show.ShowBuilder.aShow()
            .withName("Jazz Night")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(19, 0))
            .withDuration(180)
            .withEventType("CONCERT")
            .withMinPrice(40.0)
            .withMaxPrice(100.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 2")
            .withText("Test description 2")
            .build();

        showRepository.save(show1);
        showRepository.save(show2);

        // Test name filter
        MvcResult result = mockMvc.perform(get("/api/v1/shows/filter")
                .param("name", "Rock")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleShowDto[].class));

        assertEquals(1, shows.size());
        assertEquals("Rock Concert", shows.get(0).getName());
    }

    @Test
    public void filterShowsByPriceRange_returnsMatchingShows() throws Exception {
        // Create test shows with different price ranges
        Show show1 = Show.ShowBuilder.aShow()
            .withName("Expensive Show")
            .withDate(LocalDate.now().plusDays(1))  // Set future date
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(100.0)
            .withMaxPrice(200.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 1")
            .withText("Test description 1")
            .build();

        Show show2 = Show.ShowBuilder.aShow()
            .withName("Cheap Show")
            .withDate(LocalDate.now().plusDays(1))  // Set future date
            .withTime(LocalTime.of(19, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(20.0)
            .withMaxPrice(50.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 2")
            .withText("Test description 2")
            .build();

        showRepository.save(show1);
        showRepository.save(show2);

        // Test price range filter
        MvcResult result = mockMvc.perform(get("/api/v1/shows/filter")
                .param("minPrice", "30.0")
                .param("maxPrice", "150.0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleShowDto[].class));

        assertEquals(2, shows.size());
        assertEquals("Cheap Show", shows.get(0).getName());
    }

    @Test
    public void filterShowsByDateAndTime_returnsMatchingShows() throws Exception {
        LocalDate testDate = LocalDate.now().plusDays(1); // Use tomorrow's date

        Show show1 = Show.ShowBuilder.aShow()
            .withName("Morning Show")
            .withDate(testDate)
            .withTime(LocalTime.of(10, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(100.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 1")
            .withText("Test description 1")
            .build();

        Show show2 = Show.ShowBuilder.aShow()
            .withName("Evening Show")
            .withDate(testDate)
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(100.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 2")
            .withText("Test description 2")
            .build();

        showRepository.save(show1);
        showRepository.save(show2);

        // Test date and time filter
        MvcResult result = mockMvc.perform(get("/api/v1/shows/filter")
                .param("date", testDate.toString())
                .param("timeFrom", "19:00")
                .param("timeTo", "22:00")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleShowDto[].class));

        assertEquals(1, shows.size());
        assertEquals("Evening Show", shows.get(0).getName());
    }

    @Test
    public void findAll_returnsAllFutureShows() throws Exception {
        // Create test shows
        Show show1 = Show.ShowBuilder.aShow()
            .withName("Show 1")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(150.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 1")
            .withText("Test description 1")
            .build();

        Show show2 = Show.ShowBuilder.aShow()
            .withName("Show 2")
            .withDate(LocalDate.now().plusDays(2))
            .withTime(LocalTime.of(19, 0))
            .withDuration(180)
            .withEventType("CONCERT")
            .withMinPrice(40.0)
            .withMaxPrice(100.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 2")
            .withText("Test description 2")
            .build();

        showRepository.save(show1);
        showRepository.save(show2);

        MvcResult result = mockMvc.perform(get("/api/v1/shows")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleShowDto[].class));

        assertEquals(2, shows.size());
    }

    @Test
    public void getShowsWithoutEvent_returnsShowsWithNoEvent() throws Exception {
        Show show = Show.ShowBuilder.aShow()
            .withName("Show Without Event")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(150.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary")
            .withText("Test description")
            .build();

        showRepository.save(show);

        MvcResult result = mockMvc.perform(get("/api/v1/shows/available")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleShowDto[].class));

        assertEquals(1, shows.size());
    }

    @Test
    public void find_returnsShowById() throws Exception {
        Show show = Show.ShowBuilder.aShow()
            .withName("Test Show")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(150.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary")
            .withText("Test description")
            .build();

        show = showRepository.save(show);

        MvcResult result = mockMvc.perform(get("/api/v1/shows/{id}", show.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        DetailedShowDto showDto = objectMapper.readValue(
            result.getResponse().getContentAsString(), DetailedShowDto.class);

        assertEquals(show.getId(), showDto.getId());
        assertEquals(show.getName(), showDto.getName());
    }

    @Test
    public void getShowsByIds_returnsMatchingShows() throws Exception {
        Show show1 = Show.ShowBuilder.aShow()
            .withName("Show 1")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(150.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 1")
            .withText("Test description 1")
            .build();

        show1 = showRepository.save(show1);

        MvcResult result = mockMvc.perform(get("/api/v1/shows/tickets/shows")
                .param("ids", show1.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<DetailedShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), DetailedShowDto[].class));

        assertEquals(1, shows.size());
        assertEquals(show1.getId(), shows.get(0).getId());
    }

    @Test
    public void getShowsByHallId_returnsShowsForSpecificHall() throws Exception {
        // Create test shows for the test hall
        Show show1 = Show.ShowBuilder.aShow()
            .withName("Hall Show 1")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(20, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(50.0)
            .withMaxPrice(150.0)
            .withCapacity(100)
            .withSoldSeats(0)
            .withSummary("Test summary 1")
            .withText("Test description 1")
            .withHall(testHall)
            .build();

        Show show2 = Show.ShowBuilder.aShow()
            .withName("Hall Show 2")
            .withDate(LocalDate.now().plusDays(1))
            .withTime(LocalTime.of(19, 0))
            .withDuration(120)
            .withEventType("CONCERT")
            .withMinPrice(40.0)
            .withMaxPrice(100.0)
            .withCapacity(200)
            .withSoldSeats(0)
            .withSummary("Test summary 2")
            .withText("Test description 2")
            .withHall(testHall2)
            .build();

        showRepository.save(show1);
        showRepository.save(show2);

        MvcResult result = mockMvc.perform(get("/api/v1/shows/hall/{hallId}", testHall.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleShowDto> shows = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleShowDto[].class));

        assertEquals(1, shows.size());
        assertEquals("Hall Show 1", shows.get(0).getName());
    }
}