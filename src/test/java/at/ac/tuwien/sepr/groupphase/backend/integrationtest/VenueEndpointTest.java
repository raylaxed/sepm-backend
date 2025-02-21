package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class VenueEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private static final String VENUE_BASE_URI = "/api/v1/venues";
    private static final String ADMIN_USER = "admin@email.com";
    private static final List<String> ADMIN_ROLES = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

    @BeforeEach
    public void beforeEach() {
        hallRepository.deleteAll();
        venueRepository.deleteAll();
    }

    private VenueDto createValidVenueDto() {
        return new VenueDto(null, "Test Venue", "123 Street", "City", "County", "12345", List.of(1L, 2L));
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(VENUE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<VenueDto> venueDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            VenueDto[].class));

        assertEquals(0, venueDtos.size());
    }

    @Test
    public void givenValidVenue_whenCreate_thenCreated() throws Exception {
        // Arrange
        VenueDto validVenue = createValidVenueDto();
        String requestBody = objectMapper.writeValueAsString(validVenue);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post(VENUE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        VenueDto createdVenue = objectMapper.readValue(response.getContentAsString(), VenueDto.class);

        assertAll(
            () -> assertNotNull(createdVenue, "Created venue should not be null"),
            () -> assertNotNull(createdVenue.id(), "Created venue ID should not be null"),
            () -> assertEquals(validVenue.name(), createdVenue.name(), "Venue name should match")
        );
    }

    @Test
    public void givenNonExistingVenue_whenGetById_thenNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(VENUE_BASE_URI + "/{id}", 999)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void givenNoAuth_whenAccess_thenUnauthorized() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(VENUE_BASE_URI))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void filterVenues_whenSearchingByCity_returnsMatchingVenues() throws Exception {
        // Create test venues
        for (int i = 1; i <= 20; i++) {
            Venue venue = new Venue.VenueBuilder()
                .withName("Venue " + i)
                .withStreet("Street " + i)
                .withCity(i <= 10 ? "Vienna" : "Graz")
                .withCounty(i <= 10 ? "Vienna" : "Styria")
                .withPostalCode(i <= 10 ? "1010" : "8010")
                .build();
            venueRepository.save(venue);
        }

        MvcResult result = mockMvc.perform(get(VENUE_BASE_URI + "/filter")
                .param("city", "Vienna")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<VenueDto> foundVenues = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), VenueDto[].class));

        assertAll(
            () -> assertTrue(foundVenues.size() > 1, "Should find multiple venues"),
            () -> assertEquals(10, foundVenues.size(), "Should find exactly 10 venues in Vienna"),
            () -> assertTrue(foundVenues.stream()
                .allMatch(venue -> venue.city().equals("Vienna")), "All found venues should be in Vienna"),
            () -> assertTrue(foundVenues.stream()
                .allMatch(venue -> venue.postalCode().equals("1010")), "All found venues should have Vienna postal code")
        );
    }
}