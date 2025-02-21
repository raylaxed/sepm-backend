package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.StripeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import com.stripe.exception.StripeException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class OrderEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

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
    private ShowSectorRepository showSectorRepository;

    @MockBean
    private StripeService stripeService;

    private static final String ORDER_BASE_URI = "/api/v1/orders";
    private static TicketDto testTicketDto;
    private static OrderDto testOrderDto;

    private ApplicationUser testUser;


    @BeforeEach
    public void beforeEach() throws StripeException {
        orderRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        showSectorRepository.deleteAll();
        seatRepository.deleteAll();
        sectorRepository.deleteAll();
        hallRepository.deleteAll();
        showRepository.deleteAll();
        venueRepository.deleteAll();

        // Create test user
        testUser = new ApplicationUser();
        testUser.setEmail(TEST_USER_EMAIL);
        testUser.setPassword(TEST_USER_PASSWORD);
        testUser.setFirstName(TEST_USER_FIRSTNAME);
        testUser.setLastName(TEST_USER_LASTNAME);
        testUser.setAddress(TEST_USER_ADDRESS);
        testUser.setDateOfBirth(TEST_USER_DOB);
        testUser = userRepository.save(testUser);

        // Create venue
        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue.setStreet(TEST_VENUE_STREET);
        venue.setCity(TEST_VENUE_CITY);
        venue.setCounty(TEST_VENUE_COUNTY);
        venue.setPostalCode(TEST_VENUE_POSTAL_CODE);
        venue = venueRepository.save(venue);

        // Create hall
        Hall hall = new Hall();
        hall.setName("Test Hall");
        hall.setVenue(venue);
        hall.setCanvasHeight(1000);
        hall.setCanvasWidth(1000);
        hall.setCapacity(100);
        hall = hallRepository.save(hall);

        // Create sector
        Sector sector = new Sector();
        sector.setSectorName("Test Sector");
        sector.setHall(hall);
        sector.setPrice(1L);
        sector.setRows(10);
        sector.setColumns(10);
        sector = sectorRepository.save(sector);

        // Create seat
        Seat seat = new Seat();
        seat.setRowSeat(1);
        seat.setColumnSeat(1);
        seat.setPositionX(1);
        seat.setPositionY(1);
        seat.setSector(sector);
        seat = seatRepository.save(seat);

        // Create show
        Show show = new Show();
        show.setName("Test Show");
        show.setDate(LocalDate.now().plusDays(1));
        show.setTime(LocalTime.of(20, 0));
        show.setEventType("Concert");
        show.setDuration(120);
        show.setCapacity(100);
        show.setSoldSeats(0);
        show.setSummary("Test Summary");
        show.setText("Test Text");
        show.setVenue(venue);
        show.setHall(hall);
        show = showRepository.save(show);

        // Create show sector with price
        ShowSector showSector = new ShowSector();
        showSector.setShow(show);
        showSector.setSector(sector);
        showSector.setPrice(0.1);
        showSector = showSectorRepository.save(showSector);

        // Create and save test ticket
        Ticket ticket = Ticket.TicketBuilder.aTicket()
            .withTicketType("Regular")
            .withPrice(showSector.getPrice())
            .withUser(testUser)
            .withShow(show)
            .withSeat(seat)
            .withPurchased(false)
            .withReserved(false)
            .withInCart(true)
            .build();
        ticket = ticketRepository.save(ticket);

        // Create the ticket DTO from the saved ticket
        testTicketDto = new TicketDto.TicketDtoBuilder()
            .withId(ticket.getId())
            .withShowId(show.getId())
            .withPrice(ticket.getPrice())
            .withTicketType(ticket.getTicketType())
            .withPurchased(true)
            .withReserved(false)
            .withInCart(false)
            .withDate(TEST_ORDER_DATE)
            .withSeatId(seat.getSeatId())
            .withUserId(testUser.getId())
            .build();

        // Create the order DTO using the ticket DTO
        testOrderDto = new OrderDto.OrderDtoBuilder()
            .withTotal(0.1)
            .withOrderDate(TEST_ORDER_DATE)
            .withTickets(List.of(testTicketDto))
            .withUserId(testUser.getId())
            .withPaymentIntentId("pi_1234567890")
            .withCancelled(false)
            .build();

        // Mock successful refund processing
        doAnswer(invocation -> null).when(stripeService).refundPayment(anyString(), anyDouble());
    }

    @Test
    public void givenValidOrder_whenPurchaseOrder_thenReturnCreatedOrder() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(ORDER_BASE_URI + "/purchase")
                .content(objectMapper.writeValueAsString(testOrderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        OrderDto responseOrder = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderDto.class);

        assertAll(
            () -> assertNotNull(responseOrder.getId()),
            () -> assertEquals(0.1, responseOrder.getTotal()),
            () -> assertNotNull(responseOrder.getOrderDate()),
            () -> assertFalse(responseOrder.getTickets().isEmpty()),
            () -> assertEquals(1, responseOrder.getTickets().size()),
            () -> assertEquals(0.1, responseOrder.getTickets().get(0).getPrice()),
            () -> assertEquals("Regular", responseOrder.getTickets().get(0).getTicketType()),
            () -> assertTrue(responseOrder.getTickets().get(0).getPurchased())
        );
    }

    @Test
    public void givenValidTickets_whenCancelPurchase_thenReturnUpdatedOrder() throws Exception {
        // First create the order
        MvcResult createResult = mockMvc.perform(post(ORDER_BASE_URI + "/purchase")
                .content(objectMapper.writeValueAsString(testOrderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();

        OrderDto createdOrder = objectMapper.readValue(createResult.getResponse().getContentAsString(), OrderDto.class);

        // Then cancel the purchase
        Map<String, Object> cancelRequest = new HashMap<>();
        cancelRequest.put("ticketIds", List.of(createdOrder.getTickets().get(0).getId()));
        cancelRequest.put("userId", createdOrder.getUserId());

        MvcResult cancelResult = mockMvc.perform(post(ORDER_BASE_URI + "/cancelPurchase")
                .content(objectMapper.writeValueAsString(cancelRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        OrderDto cancelledOrder = objectMapper.readValue(cancelResult.getResponse().getContentAsString(), OrderDto.class);

        assertAll(
            () -> assertTrue(cancelledOrder.getCancelled()),
            () -> assertEquals(createdOrder.getId(), cancelledOrder.getId()),
            () -> assertEquals(createdOrder.getTotal(), cancelledOrder.getTotal()),
            () -> assertEquals(createdOrder.getUserId(), cancelledOrder.getUserId())
        );
    }

    @Test
    public void givenValidUserId_whenGetOrders_thenReturnUserOrders() throws Exception {
        // First create the order
        MvcResult createResult = mockMvc.perform(post(ORDER_BASE_URI + "/purchase")
                .content(objectMapper.writeValueAsString(testOrderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();

        OrderDto createdOrder = objectMapper.readValue(createResult.getResponse().getContentAsString(), OrderDto.class);

        // Then get orders for user
        MvcResult getResult = mockMvc.perform(get(ORDER_BASE_URI + "/user/" + createdOrder.getUserId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<OrderDto> orders = objectMapper.readValue(
            getResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDto.class)
        );

        assertAll(
            () -> assertFalse(orders.isEmpty()),
            () -> assertEquals(1, orders.size()),
            () -> assertEquals(createdOrder.getId(), orders.get(0).getId()),
            () -> assertEquals(createdOrder.getTotal(), orders.get(0).getTotal())
        );
    }

    @Test
    public void givenValidOrderId_whenGenerateOrderPdf_thenReturnPdf() throws Exception {
        // First create the order
        MvcResult createResult = mockMvc.perform(post(ORDER_BASE_URI + "/purchase")
                .content(objectMapper.writeValueAsString(testOrderDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andExpect(status().isCreated())
            .andReturn();

        OrderDto createdOrder = objectMapper.readValue(createResult.getResponse().getContentAsString(), OrderDto.class);

        mockMvc.perform(get(ORDER_BASE_URI + "/" + createdOrder.getId() + "/pdf")
                .param("userId", createdOrder.getUserId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(result -> assertEquals("application/pdf", result.getResponse().getContentType()))
            .andExpect(result -> assertTrue(result.getResponse().getHeader("Content-Disposition")
                .contains("attachment; filename=\"order-" + createdOrder.getId() + ".pdf\"")));
    }

    @Test
    public void whenGetOrdersWithoutAuth_thenReturn403() throws Exception {
        mockMvc.perform(get(ORDER_BASE_URI + "/user/1"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidUserId_whenGetOrders_thenReturn404() throws Exception {
        mockMvc.perform(get(ORDER_BASE_URI + "/user/999999")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}