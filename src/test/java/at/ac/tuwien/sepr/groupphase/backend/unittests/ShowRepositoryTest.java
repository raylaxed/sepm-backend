package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ShowRepositoryTest implements TestData {

    @Autowired
    private ShowRepository showRepository;

    @Test
    public void givenNothing_whenSaveShow_thenFindListWithOneElementAndFindShowById() {
        Show show = Show.ShowBuilder.aShow()
            .withName(TEST_SHOW_NAME)
            .withSummary(TEST_SHOW_SUMMARY)
            .withText(TEST_SHOW_TEXT)
            .withDuration(TEST_SHOW_DURATION)
            .withEventType(TEST_SHOW_TYPE)
            .withImageUrl(TEST_SHOW_IMAGE)
            .withDate(TEST_SHOW_DATE)
            .withTime(TEST_SHOW_TIME)
            .withCapacity(TEST_SHOW_CAPACITY)
            .withSoldSeats(TEST_SHOW_SOLD_SEATS)
            .build();

        showRepository.save(show);

        assertAll(
            () -> assertEquals(1, showRepository.findAll().size()),
            () -> assertNotNull(showRepository.findById(show.getId())),
            () -> assertEquals(TEST_SHOW_NAME, showRepository.findById(show.getId()).get().getName()),
            () -> assertEquals(TEST_SHOW_SUMMARY, showRepository.findById(show.getId()).get().getSummary()),
            () -> assertEquals(TEST_SHOW_TEXT, showRepository.findById(show.getId()).get().getText())
        );
    }

    @Test
    public void givenShow_whenDelete_thenShowIsDeleted() {
        Show show = Show.ShowBuilder.aShow()
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

        Show savedShow = showRepository.save(show);
        showRepository.deleteById(savedShow.getId());

        assertTrue(showRepository.findById(savedShow.getId()).isEmpty());
    }

    @Test
    public void givenShow_whenUpdate_thenShowIsUpdated() {
        Show show = Show.ShowBuilder.aShow()
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

        Show savedShow = showRepository.save(show);
        savedShow.setName("Updated Name");
        showRepository.save(savedShow);

        Optional<Show> found = showRepository.findById(savedShow.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getName());
    }

    @Test
    public void givenMultipleShows_whenFindAll_thenReturnAllShows() {
        Show show1 = Show.ShowBuilder.aShow()
            .withName("Show 1")
            .withSummary(TEST_SHOW_SUMMARY)
            .withText(TEST_SHOW_TEXT)
            .withDuration(TEST_SHOW_DURATION)
            .withEventType(TEST_SHOW_TYPE)
            .withDate(TEST_SHOW_DATE)
            .withTime(TEST_SHOW_TIME)
            .withCapacity(TEST_SHOW_CAPACITY)
            .withSoldSeats(TEST_SHOW_SOLD_SEATS)
            .build();

        Show show2 = Show.ShowBuilder.aShow()
            .withName("Show 2")
            .withSummary(TEST_SHOW_SUMMARY)
            .withText(TEST_SHOW_TEXT)
            .withDuration(TEST_SHOW_DURATION)
            .withEventType(TEST_SHOW_TYPE)
            .withDate(TEST_SHOW_DATE)
            .withTime(TEST_SHOW_TIME)
            .withCapacity(TEST_SHOW_CAPACITY)
            .withSoldSeats(TEST_SHOW_SOLD_SEATS)
            .build();

        showRepository.save(show1);
        showRepository.save(show2);

        List<Show> shows = showRepository.findAll();
        assertEquals(2, shows.size());
    }
}

