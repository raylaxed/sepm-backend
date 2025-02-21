package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleShowDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShowMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ShowMappingTest implements TestData {

    private final Show show = Show.ShowBuilder.aShow()
        .withId(ID)
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

    @Autowired
    private ShowMapper showMapper;

    @Test
    public void givenNothing_whenMapDetailedShowDtoToEntity_thenEntityHasAllProperties() {
        DetailedShowDto detailedShowDto = showMapper.showToDetailedShowDto(show);
        assertAll(
            () -> assertEquals(ID, detailedShowDto.getId()),
            () -> assertEquals(TEST_SHOW_NAME, detailedShowDto.getName()),
            () -> assertEquals(TEST_SHOW_SUMMARY, detailedShowDto.getSummary()),
            () -> assertEquals(TEST_SHOW_TEXT, detailedShowDto.getText()),
            () -> assertEquals(TEST_SHOW_DURATION, detailedShowDto.getDuration()),
            () -> assertEquals(TEST_SHOW_TYPE, detailedShowDto.getEventType()),
            () -> assertEquals(TEST_SHOW_IMAGE, detailedShowDto.getImageUrl()),
            () -> assertEquals(TEST_SHOW_DATE, detailedShowDto.getDate()),
            () -> assertEquals(TEST_SHOW_TIME, detailedShowDto.getTime()),
            () -> assertEquals(TEST_SHOW_CAPACITY, detailedShowDto.getCapacity()),
            () -> assertEquals(TEST_SHOW_SOLD_SEATS, detailedShowDto.getSoldSeats())
        );
    }

    @Test
    public void givenNothing_whenMapListWithTwoShowEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Show> shows = new ArrayList<>();
        shows.add(show);
        shows.add(show);

        List<SimpleShowDto> simpleShowDtos = showMapper.showToSimpleShowDto(shows);
        assertEquals(2, simpleShowDtos.size());
        SimpleShowDto simpleShowDto = simpleShowDtos.get(0);
        assertAll(
            () -> assertEquals(ID, simpleShowDto.getId()),
            () -> assertEquals(TEST_SHOW_NAME, simpleShowDto.getName()),
            () -> assertEquals(TEST_SHOW_SUMMARY, simpleShowDto.getSummary()),
            () -> assertEquals(TEST_SHOW_TYPE, simpleShowDto.getEventType()),
            () -> assertEquals(TEST_SHOW_DATE, simpleShowDto.getDate()),
            () -> assertEquals(TEST_SHOW_TIME, simpleShowDto.getTime())
        );
    }

    @Test
    public void givenNothing_whenMapSingleShowToSimpleDto_thenGetAllProperties() {
        SimpleShowDto simpleShowDto = showMapper.showToSimpleShowDto(show);
        assertAll(
            () -> assertEquals(ID, simpleShowDto.getId()),
            () -> assertEquals(TEST_SHOW_NAME, simpleShowDto.getName()),
            () -> assertEquals(TEST_SHOW_SUMMARY, simpleShowDto.getSummary()),
            () -> assertEquals(TEST_SHOW_TYPE, simpleShowDto.getEventType()),
            () -> assertEquals(TEST_SHOW_DATE, simpleShowDto.getDate()),
            () -> assertEquals(TEST_SHOW_TIME, simpleShowDto.getTime())
        );
    }

    @Test
    public void givenNull_whenMapListOfShows_thenGetNull() {
        List<Show> nullList = null;
        List<SimpleShowDto> simpleShowDtos = showMapper.showToSimpleShowDto(nullList);
        assertNull(simpleShowDtos);
    }

    @Test
    public void givenNull_whenMapSingleShow_thenGetNull() {
        SimpleShowDto simpleShowDto = showMapper.showToSimpleShowDto((Show) null);
        assertNull(simpleShowDto);

        DetailedShowDto detailedShowDto = showMapper.showToDetailedShowDto(null);
        assertNull(detailedShowDto);
    }
}
