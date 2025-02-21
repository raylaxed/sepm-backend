package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleVenueService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VenueServiceTest {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SimpleVenueService venueService;

    private static Venue testVenue;

    @BeforeAll
    static void beforeAll(@Autowired VenueRepository venueRepository) {
        // Create and save test venue
        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue.setStreet("123 Test Street");
        venue.setCity("Test City");
        venue.setCounty("Test Country");
        venue.setPostalCode("12345");
        venue.setHallIds(List.of(1L, 2L));

        testVenue = venueRepository.save(venue);
    }

    @AfterAll
    static void afterAll(@Autowired VenueRepository venueRepository) {
        venueRepository.deleteAll();
    }

    @Test
    void givenValidVenueDto_whenCreate_thenReturnCreatedVenue() throws ValidationException, ConflictException {
        // Arrange
        VenueDto venueDto = new VenueDto(
            null,
            "New Venue",
            "456 New Street",
            "New City",
            "New Country",
            "67890",
            List.of(3L, 4L)
        );

        // Act
        Venue created = venueService.createVenue(venueDto);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(venueDto.name(), created.getName());
        assertEquals(venueDto.street(), created.getStreet());
        assertEquals(venueDto.city(), created.getCity());
        assertEquals(venueDto.county(), created.getCounty());
        assertEquals(venueDto.postalCode(), created.getPostalCode());
        assertEquals(venueDto.hallIds(), created.getHallIds());
    }

    @Test
    void givenExistingId_whenFindOne_thenReturnVenue() {
        // Act
        Venue found = venueService.findOne(testVenue.getId());

        // Assert
        assertNotNull(found);
        assertEquals(testVenue.getId(), found.getId());
        assertEquals(testVenue.getName(), found.getName());
        assertEquals(testVenue.getStreet(), found.getStreet());
    }

    @Test
    void givenExistingId_whenDelete_thenSuccess() {
        // Arrange
        Venue newVenue = new Venue();
        newVenue.setName("Venue To Delete");
        newVenue.setStreet("Delete Street");
        newVenue.setCity("Delete City");
        newVenue.setCounty("Delete Country");
        newVenue.setPostalCode("67890");
        newVenue.setHallIds(List.of(5L, 6L));   
        Venue savedVenue = venueRepository.save(newVenue);

        // Act & Assert
        assertDoesNotThrow(() -> venueService.deleteVenue(savedVenue.getId()));
        assertFalse(venueRepository.existsById(savedVenue.getId()));
    }

    @Test
    void givenNonExistingId_whenDelete_thenThrowNotFoundException() {
        // Act & Assert
        assertThrows(NotFoundException.class, () -> venueService.deleteVenue(999L));
    }
} 