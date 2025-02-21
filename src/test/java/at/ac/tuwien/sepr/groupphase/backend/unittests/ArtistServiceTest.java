package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ArtistServiceTest implements TestData {

    @Mock
    private ArtistRepository artistRepository;

    private SimpleArtistService artistService;

    @BeforeEach
    void setUp() {
        artistService = new SimpleArtistService(artistRepository, "/images", "/images/");
    }

    @Test
    void createArtist_ValidArtist_ReturnsCreatedArtist() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName(TEST_ARTIST_NAME)
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();

        when(artistRepository.save(any(Artist.class))).thenReturn(artist);

        Artist created = artistService.createArtist(artist);

        assertAll(
            () -> assertNotNull(created),
            () -> assertEquals(TEST_ARTIST_NAME, created.getName()),
            () -> assertEquals(TEST_ARTIST_SUMMARY, created.getSummary()),
            () -> verify(artistRepository, times(1)).save(any(Artist.class))
        );
    }

    @Test
    void findOne_ExistingArtist_ReturnsArtist() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withId(1L)
            .withName(TEST_ARTIST_NAME)
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();

        when(artistRepository.findByIdWithShows(1L)).thenReturn(Optional.of(artist));

        Artist found = artistService.findOne(1L);

        assertAll(
            () -> assertNotNull(found),
            () -> assertEquals(1L, found.getId()),
            () -> assertEquals(TEST_ARTIST_NAME, found.getName())
        );
    }

    @Test
    void findOne_NonExistingArtist_ThrowsNotFoundException() {
        when(artistRepository.findByIdWithShows(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> artistService.findOne(1L));
    }

    @Test
    void findOne_WithShows_FiltersPastShows() {
        Set<Show> shows = new HashSet<>();
        // Past show
        Show pastShow = new Show();
        pastShow.setDate(LocalDate.now().minusDays(1));
        pastShow.setTime(LocalTime.now());
        shows.add(pastShow);

        // Future show
        Show futureShow = new Show();
        futureShow.setDate(LocalDate.now().plusDays(1));
        futureShow.setTime(LocalTime.now());
        shows.add(futureShow);

        Artist artist = Artist.ArtistBuilder.anArtist()
            .withId(1L)
            .withName(TEST_ARTIST_NAME)
            .withShows(shows)
            .build();

        when(artistRepository.findByIdWithShows(1L)).thenReturn(Optional.of(artist));

        Artist found = artistService.findOne(1L);

        assertEquals(1, found.getShows().size());
    }

    @Test
    void searchArtistsByName_WithValidQuery_ReturnsMatchingArtists() {
        List<Artist> artists = Arrays.asList(
            Artist.ArtistBuilder.anArtist()
                .withName("John Doe")
                .withSummary(TEST_ARTIST_SUMMARY)
                .withText(TEST_ARTIST_TEXT)
                .build(),
            Artist.ArtistBuilder.anArtist()
                .withName("John Smith")
                .withSummary(TEST_ARTIST_SUMMARY)
                .withText(TEST_ARTIST_TEXT)
                .build()
        );

        when(artistRepository.findByNameContainingIgnoreCase("John")).thenReturn(artists);

        List<Artist> results = artistService.searchArtistsByName("John");

        assertAll(
            () -> assertEquals(2, results.size()),
            () -> assertTrue(results.stream().allMatch(a -> a.getName().contains("John")))
        );
    }

    @Test
    void searchArtistsByName_WithEmptyQuery_ReturnsAllArtists() {
        List<Artist> allArtists = Arrays.asList(
            Artist.ArtistBuilder.anArtist()
                .withName("Artist 1")
                .withSummary(TEST_ARTIST_SUMMARY)
                .withText(TEST_ARTIST_TEXT)
                .build(),
            Artist.ArtistBuilder.anArtist()
                .withName("Artist 2")
                .withSummary(TEST_ARTIST_SUMMARY)
                .withText(TEST_ARTIST_TEXT)
                .build()
        );

        when(artistRepository.findAll()).thenReturn(allArtists);

        List<Artist> results = artistService.searchArtistsByName("");

        assertEquals(2, results.size());
    }
}