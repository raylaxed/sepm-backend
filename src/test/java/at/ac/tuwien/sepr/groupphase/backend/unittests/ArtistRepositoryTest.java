package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ArtistRepositoryTest implements TestData {

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    public void givenNothing_whenSaveArtist_thenFindListWithOneElementAndFindArtistById() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName(TEST_ARTIST_NAME)
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .withImageUrl(TEST_ARTIST_IMAGE)
            .build();

        artistRepository.save(artist);

        assertAll(
            () -> assertEquals(1, artistRepository.findAll().size()),
            () -> assertNotNull(artistRepository.findById(artist.getId()))
        );
    }

    @Test
    public void givenArtist_whenSearchByName_thenFindArtist() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName("The Beatles")
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .withImageUrl(TEST_ARTIST_IMAGE)
            .build();
        artistRepository.save(artist);

        assertAll(
            () -> assertEquals(1, artistRepository.findByNameContainingIgnoreCase("beatles").size()),
            () -> assertEquals(1, artistRepository.findByNameContainingIgnoreCase("THE").size()),
            () -> assertEquals(0, artistRepository.findByNameContainingIgnoreCase("queen").size())
        );
    }

    @Test
    public void givenMultipleArtists_whenSearchByName_thenFindMatchingArtists() {
        Artist artist1 = Artist.ArtistBuilder.anArtist()
            .withName("The Beatles")
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();
        Artist artist2 = Artist.ArtistBuilder.anArtist()
            .withName("The Rolling Stones")
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();
        Artist artist3 = Artist.ArtistBuilder.anArtist()
            .withName("Queen")
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();

        artistRepository.save(artist1);
        artistRepository.save(artist2);
        artistRepository.save(artist3);

        assertAll(
            () -> assertEquals(2, artistRepository.findByNameContainingIgnoreCase("the").size()),
            () -> assertEquals(1, artistRepository.findByNameContainingIgnoreCase("queen").size()),
            () -> assertEquals(0, artistRepository.findByNameContainingIgnoreCase("xyz").size())
        );
    }

    @Test
    public void givenArtist_whenUpdate_thenFindUpdatedArtist() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName(TEST_ARTIST_NAME)
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();
        artist = artistRepository.save(artist);

        String newName = "Updated Name";
        artist.setName(newName);
        artistRepository.save(artist);

        Artist found = artistRepository.findById(artist.getId()).orElseThrow();
        assertEquals(newName, found.getName());
    }

    @Test
    public void givenArtist_whenDelete_thenArtistNotFound() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName(TEST_ARTIST_NAME)
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();
        artist = artistRepository.save(artist);

        Long id = artist.getId();
        artistRepository.deleteById(id);

        assertTrue(artistRepository.findById(id).isEmpty());
    }
}

