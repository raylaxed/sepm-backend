package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.*;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TicketEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private ApplicationUser testUser;
    private Show testShow;
    private Seat testSeat;
    private TicketDto testTicketDto;

    @BeforeEach
    public void beforeEach() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new ApplicationUser();
        testUser.setEmail("admin@email.com");
        testUser.setPassword("password");
        testUser.setFirstName("Admin");
        testUser.setLastName("Admin");
        testUser.setAdmin(true);
        testUser = userRepository.save(testUser);

        // Create venue and hall
        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue.setCity("Test City");
        venue.setCounty("Test County");
        venue.setPostalCode("1234");
        venue.setStreet("Test Street");
        venue = venueRepository.save(venue);

        Hall hall = new Hall();
        hall.setName("Test Hall");
        hall.setVenue(venue);
        hall.setCapacity(100);
        hall.setCanvasHeight(800);
        hall.setCanvasWidth(1200);
        hall = hallRepository.save(hall);

        // Create sector and seat
        Sector sector = new Sector();
        sector.setSectorName("Test Sector");
        sector.setHall(hall);
        sector.setPrice(1L);
        sector.setRows(10);
        sector.setColumns(10);
        sector = sectorRepository.save(sector);

        testSeat = new Seat();
        testSeat.setSector(sector);
        testSeat.setRowSeat(1);
        testSeat.setColumnSeat(1);
        testSeat.setPositionX(1);
        testSeat.setPositionY(1);
        testSeat = seatRepository.save(testSeat);

        // Create show with explicit time setting
        testShow = new Show();
        testShow.setName("Test Show");
        testShow.setDate(LocalDate.now().plusDays(1));
        testShow.setTime(LocalTime.of(14, 0));
        testShow.setEventType("Concert");
        testShow.setDuration(120);
        testShow.setCapacity(100);
        testShow.setSoldSeats(0);
        testShow.setSummary("Test Summary");
        testShow.setText("Test Text");
        testShow.setVenue(venue);
        testShow.setHall(hall);
        testShow = showRepository.save(testShow);

        // Create test ticket DTO
        testTicketDto = new TicketDto.TicketDtoBuilder()
            .withShowId(testShow.getId())
            .withSeatId(testSeat.getSeatId())
            .withStandingSectorId(null)  // Explicitly set standing sector to null
            .withPrice(10.0)
            .withTicketType("Regular")
            .withPurchased(false)
            .withReserved(true)
            .withInCart(false)
            .withDate(LocalDateTime.now())
            .withUserId(testUser.getId())
            .withOrderId(null)
            .build();
    }

    @Test
    public void givenValidTicket_whenCreateTicket_thenTicketCreated() throws Exception {
        String body = objectMapper.writeValueAsString(List.of(testTicketDto));

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/tickets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        List<TicketDto> ticketDtos = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, TicketDto.class)
        );

        assertEquals(1, ticketDtos.size());
        TicketDto createdTicket = ticketDtos.get(0);
        assertNotNull(createdTicket.getId());
        assertEquals(testTicketDto.getShowId(), createdTicket.getShowId());
        assertEquals(testTicketDto.getSeatId(), createdTicket.getSeatId());
        assertEquals(testTicketDto.getPrice(), createdTicket.getPrice());
        assertEquals(testTicketDto.getTicketType(), createdTicket.getTicketType());
    }

    @Test
    public void givenReservedTickets_whenAddToCart_thenTicketsAddedSuccessfully() throws Exception {

        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(false);
        ticket.setReserved(true);
        ticket.setInCart(false);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        assertNotNull(ticket.getId());

        // Verify the ticket exists in the database
        List<Ticket> savedTickets = ticketRepository.findAll();
        assertEquals(1, savedTickets.size());
        assertEquals(testUser.getId(), savedTickets.get(0).getUser().getId());
        assertTrue(savedTickets.get(0).getReserved());
        assertFalse(savedTickets.get(0).getInCart());

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ticketIds", List.of(ticket.getId()));
        requestBody.put("userId", testUser.getId());

        String body = objectMapper.writeValueAsString(requestBody);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/tickets/addToCart")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<TicketDto> returnedTickets = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, TicketDto.class)
        );

        // Verify response
        assertEquals(1, returnedTickets.size());
        TicketDto returnedTicket = returnedTickets.get(0);
        assertEquals(ticket.getId(), returnedTicket.getId());
        assertTrue(returnedTicket.getInCart());
        assertFalse(returnedTicket.getReserved());
        assertFalse(returnedTicket.getPurchased());

        // Verify database state
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertTrue(updatedTicket.getInCart());
        assertFalse(updatedTicket.getReserved());
        assertFalse(updatedTicket.getPurchased());
        assertNotNull(updatedTicket.getUser());
        assertEquals(testUser.getId(), updatedTicket.getUser().getId());
    }

    @Test
    public void givenNonexistentTicket_whenAddToCart_thenThrowsNotFound() throws Exception {
        // Prepare request body with non-existent ticket ID
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ticketIds", List.of(999999L));
        requestBody.put("userId", testUser.getId());

        String body = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(post("/api/v1/tickets/addToCart")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenUserWithTickets_whenGetUserTickets_thenReturnTickets() throws Exception {
        // First create a ticket for our test user
        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(false);
        ticket.setReserved(false);
        ticket.setInCart(true);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        Long ticketId = ticket.getId();
        assertNotNull(ticket.getId());

        // Verify the ticket exists in the database
        List<Ticket> savedTickets = ticketRepository.findAll();
        assertEquals(1, savedTickets.size());
        assertEquals(testUser.getId(), savedTickets.get(0).getUser().getId());
        assertFalse(savedTickets.get(0).getReserved());
        assertTrue(savedTickets.get(0).getInCart());

        // Perform GET request
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/tickets/user/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<TicketDto> returnedTickets = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, TicketDto.class)
        );

        // Verify response
        assertEquals(1, returnedTickets.size());
        TicketDto returnedTicket = returnedTickets.get(0);
        assertAll(
            () -> assertEquals(ticketId, returnedTicket.getId()),
            () -> assertEquals(testShow.getId(), returnedTicket.getShowId()),
            () -> assertEquals(testSeat.getSeatId(), returnedTicket.getSeatId()),
            () -> assertEquals(10.0, returnedTicket.getPrice()),
            () -> assertEquals("REGULAR", returnedTicket.getTicketType()),
            () -> assertFalse(returnedTicket.getReserved()),
            () -> assertFalse(returnedTicket.getPurchased()),
            () -> assertTrue(returnedTicket.getInCart()),
            () -> assertEquals(testUser.getId(), returnedTicket.getUserId())
        );
    }

    @Test
    public void givenReservedTicket_whenCancelReservation_thenTicketCancelled() throws Exception {
        // Create a reserved ticket
        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(false);
        ticket.setReserved(true);
        ticket.setInCart(false);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        Long ticketId = ticket.getId();
        assertNotNull(ticketId);

        // Verify the ticket exists and is reserved
        List<Ticket> savedTickets = ticketRepository.findAll();
        assertEquals(1, savedTickets.size());
        assertTrue(savedTickets.get(0).getReserved());

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ticketId", ticketId);
        requestBody.put("userId", testUser.getId());

        // Perform cancel reservation request
        mockMvc.perform(post("/api/v1/tickets/cancelReservation")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk());

        // Verify ticket was deleted
        assertTrue(ticketRepository.findById(ticketId).isEmpty());
    }

    @Test
    public void givenNonReservedTicket_whenCancelReservation_thenNotFound() throws Exception {
        // Create a non-reserved ticket (in cart instead)
        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(false);
        ticket.setReserved(false);
        ticket.setInCart(true);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        Long ticketId = ticket.getId();

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ticketId", ticketId);
        requestBody.put("userId", testUser.getId());

        // Attempt to cancel non-reserved ticket
        mockMvc.perform(post("/api/v1/tickets/cancelReservation")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenTicketInCart_whenRemoveFromCart_thenTicketRemoved() throws Exception {
        // Create a ticket in cart
        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(false);
        ticket.setReserved(false);
        ticket.setInCart(true);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        Long ticketId = ticket.getId();
        assertNotNull(ticketId);

        // Verify the ticket exists and is in cart
        List<Ticket> savedTickets = ticketRepository.findAll();
        assertEquals(1, savedTickets.size());
        assertTrue(savedTickets.get(0).getInCart());

        // Perform remove from cart request
        mockMvc.perform(delete("/api/v1/tickets/cart/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isNoContent());

        // Verify ticket was deleted
        assertTrue(ticketRepository.findById(ticketId).isEmpty());
    }

    @Test
    public void givenTicketNotInCart_whenRemoveFromCart_thenNotFound() throws Exception {
        // Create a reserved ticket (not in cart)
        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(false);
        ticket.setReserved(true);
        ticket.setInCart(false);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        Long ticketId = ticket.getId();

        // Attempt to remove non-cart ticket
        mockMvc.perform(delete("/api/v1/tickets/cart/" + ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidTicketId_whenGenerateTicketPdf_thenReturnPdf() throws Exception {
        // Create a ticket first
        Ticket ticket = new Ticket();
        ticket.setUser(testUser);
        ticket.setShow(testShow);
        ticket.setSeat(testSeat);
        ticket.setPrice(10.0);
        ticket.setTicketType("REGULAR");
        ticket.setPurchased(true);
        ticket.setReserved(false);
        ticket.setInCart(false);
        ticket.setDate(LocalDateTime.now());
        ticket.setOrder(null);
        ticket.setStandingSector(null);

        ticket = ticketRepository.save(ticket);
        Long ticketId = ticket.getId();

        // Test PDF generation
        mockMvc.perform(get("/api/v1/tickets/{ticketId}/pdf", ticketId)
                .param("userId", testUser.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(testUser.getEmail(), USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(result -> assertEquals("application/pdf", result.getResponse().getContentType()))
            .andExpect(result -> assertTrue(result.getResponse().getHeader("Content-Disposition")
                .contains("attachment; filename=\"ticket-" + ticketId + ".pdf\"")));
    }
}