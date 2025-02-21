package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowSectorService;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ShowServiceTest implements TestData {

    @Mock
    private ShowRepository showRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private ShowSectorService showSectorService;
    @Mock
    private TicketRepository ticketRepository;

    private SimpleShowService showService;

    @BeforeEach
    void setUp() {
        showService = new SimpleShowService(showRepository, artistRepository, venueRepository, "/images", "/images/", hallRepository, showSectorService, ticketRepository);
    }

    @Test
    void findOne_ExistingShow_ReturnsShow() {
        Show show = Show.ShowBuilder.aShow()
            .withId(1L)
            .withName(TEST_SHOW_NAME)
            .withSummary(TEST_SHOW_SUMMARY)
            .withText(TEST_SHOW_TEXT)
            .withDuration(TEST_SHOW_DURATION)
            .withEventType(TEST_SHOW_TYPE)
            .withDate(TEST_SHOW_DATE)
            .withTime(TEST_SHOW_TIME)
            .withCapacity(TEST_SHOW_CAPACITY)
            .withSoldSeats(TEST_SHOW_SOLD_SEATS)
            .build();

        when(showRepository.findByIdWithArtists(1L)).thenReturn(Optional.of(show));

        Show found = showService.findOne(1L);

        assertAll(
            () -> assertNotNull(found),
            () -> assertEquals(1L, found.getId()),
            () -> assertEquals(TEST_SHOW_NAME, found.getName()),
            () -> assertEquals(TEST_SHOW_SUMMARY, found.getSummary()),
            () -> assertEquals(TEST_SHOW_TEXT, found.getText()),
            () -> assertEquals(TEST_SHOW_DURATION, found.getDuration()),
            () -> assertEquals(TEST_SHOW_TYPE, found.getEventType()),
            () -> assertEquals(TEST_SHOW_DATE, found.getDate()),
            () -> assertEquals(TEST_SHOW_TIME, found.getTime()),
            () -> assertEquals(TEST_SHOW_CAPACITY, found.getCapacity()),
            () -> assertEquals(TEST_SHOW_SOLD_SEATS, found.getSoldSeats())
        );
    }

    @Test
    void findOne_NonExistingShow_ThrowsNotFoundException() {
        when(showRepository.findByIdWithArtists(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> showService.findOne(1L));
    }

    @Test
    void findAll_ReturnsAllShows() {
        List<Show> shows = Arrays.asList(
            Show.ShowBuilder.aShow()
                .withId(1L)
                .withName(TEST_SHOW_NAME)
                .withSummary(TEST_SHOW_SUMMARY)
                .withText(TEST_SHOW_TEXT)
                .withDuration(TEST_SHOW_DURATION)
                .withEventType(TEST_SHOW_TYPE)
                .withDate(TEST_SHOW_DATE)
                .withTime(TEST_SHOW_TIME)
                .withCapacity(TEST_SHOW_CAPACITY)
                .withSoldSeats(TEST_SHOW_SOLD_SEATS)
                .build(),
            Show.ShowBuilder.aShow()
                .withId(2L)
                .withName("Another Show")
                .withSummary(TEST_SHOW_SUMMARY)
                .withText(TEST_SHOW_TEXT)
                .withDuration(TEST_SHOW_DURATION)
                .withEventType(TEST_SHOW_TYPE)
                .withDate(TEST_SHOW_DATE)
                .withTime(TEST_SHOW_TIME)
                .withCapacity(TEST_SHOW_CAPACITY)
                .withSoldSeats(TEST_SHOW_SOLD_SEATS)
                .build()
        );

        when(showRepository.findAllByOrderByDateAscTimeAsc()).thenReturn(shows);

        List<Show> result = showService.findAll();

        assertAll(
            () -> assertEquals(2, result.size()),
            () -> assertEquals(TEST_SHOW_NAME, result.get(0).getName()),
            () -> assertEquals("Another Show", result.get(1).getName())
        );
    }

    @Test
    void findShowsWithoutEvent_ReturnsFilteredShows() {
        List<Show> shows = Arrays.asList(
            Show.ShowBuilder.aShow()
                .withId(1L)
                .withName(TEST_SHOW_NAME)
                .withSummary(TEST_SHOW_SUMMARY)
                .withText(TEST_SHOW_TEXT)
                .withDuration(TEST_SHOW_DURATION)
                .withEventType(TEST_SHOW_TYPE)
                .withDate(TEST_SHOW_DATE)
                .withTime(TEST_SHOW_TIME)
                .withCapacity(TEST_SHOW_CAPACITY)
                .withSoldSeats(TEST_SHOW_SOLD_SEATS)
                .build()
        );

        when(showRepository.findAllByEventIsNullWithFilters(
            eq("test"), 
            eq(LocalDate.now()), 
            eq(LocalDate.now().plusDays(7))
        )).thenReturn(shows);

        List<Show> result = showService.findShowsWithoutEvent("test", LocalDate.now(), LocalDate.now().plusDays(7));

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertEquals(TEST_SHOW_NAME, result.get(0).getName())
        );
    }

    @Test
    void findShowsByHallId_ReturnsShowsInHall() {
        List<Show> shows = Arrays.asList(
            Show.ShowBuilder.aShow()
                .withId(1L)
                .withName(TEST_SHOW_NAME)
                .withSummary(TEST_SHOW_SUMMARY)
                .withText(TEST_SHOW_TEXT)
                .withDuration(TEST_SHOW_DURATION)
                .withEventType(TEST_SHOW_TYPE)
                .withDate(TEST_SHOW_DATE)
                .withTime(TEST_SHOW_TIME)
                .withCapacity(TEST_SHOW_CAPACITY)
                .withSoldSeats(TEST_SHOW_SOLD_SEATS)
                .build()
        );

        when(showRepository.findByHallId(1L)).thenReturn(shows);

        List<Show> result = showService.findShowsByHallId(1L);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertEquals(TEST_SHOW_NAME, result.get(0).getName())
        );
    }
}