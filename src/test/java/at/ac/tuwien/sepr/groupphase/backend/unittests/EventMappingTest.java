package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
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
public class EventMappingTest implements TestData {

    private final Event event = Event.EventBuilder.anEvent()
        .withId(ID)
        .withName(TEST_EVENT_NAME)
        .withSummary(TEST_EVENT_SUMMARY)
        .withText(TEST_EVENT_TEXT)
        .withDurationFrom(TEST_EVENT_DURATION_FROM)
        .withDurationTo(TEST_EVENT_DURATION_TO)
        .withType(TEST_EVENT_TYPE)
        .withImageUrl(TEST_EVENT_IMAGE)
        .withSoldSeats(TEST_EVENT_SOLD_SEATS)
        .build();

    @Autowired
    private EventMapper eventMapper;

    @Test
    public void givenNothing_whenMapDetailedEventDtoToEntity_thenEntityHasAllProperties() {
        DetailedEventDto detailedEventDto = eventMapper.eventToDetailedEventDto(event);
        assertAll(
            () -> assertEquals(ID, detailedEventDto.getId()),
            () -> assertEquals(TEST_EVENT_NAME, detailedEventDto.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, detailedEventDto.getSummary()),
            () -> assertEquals(TEST_EVENT_TEXT, detailedEventDto.getText()),
            () -> assertEquals(TEST_EVENT_DURATION_FROM, detailedEventDto.getDurationFrom()),
            () -> assertEquals(TEST_EVENT_DURATION_TO, detailedEventDto.getDurationTo()),
            () -> assertEquals(TEST_EVENT_TYPE, detailedEventDto.getType()),
            () -> assertEquals(TEST_EVENT_IMAGE, detailedEventDto.getImageUrl()),
            () -> assertEquals(TEST_EVENT_SOLD_SEATS, detailedEventDto.getSoldSeats())
        );
    }

    @Test
    public void givenNothing_whenMapListWithTwoEventEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event);

        List<SimpleEventDto> simpleEventDtos = eventMapper.eventToSimpleEventDto(events);
        assertEquals(2, simpleEventDtos.size());
        SimpleEventDto simpleEventDto = simpleEventDtos.get(0);
        assertAll(
            () -> assertEquals(ID, simpleEventDto.getId()),
            () -> assertEquals(TEST_EVENT_NAME, simpleEventDto.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, simpleEventDto.getSummary()),
            () -> assertEquals(TEST_EVENT_TYPE, simpleEventDto.getType()),
            () -> assertEquals(TEST_EVENT_SOLD_SEATS, simpleEventDto.getSoldSeats())
        );
    }

    @Test
    public void givenNothing_whenMapEventInquiryDtoToEntity_thenEntityHasAllProperties() {
        EventInquiryDto inquiryDto = new EventInquiryDto();
        inquiryDto.setName(TEST_EVENT_NAME);
        inquiryDto.setSummary(TEST_EVENT_SUMMARY);
        inquiryDto.setText(TEST_EVENT_TEXT);
        inquiryDto.setDurationFrom(TEST_EVENT_DURATION_FROM);
        inquiryDto.setDurationTo(TEST_EVENT_DURATION_TO);
        inquiryDto.setType(TEST_EVENT_TYPE);
        inquiryDto.setImageUrl(TEST_EVENT_IMAGE);

        Event event = eventMapper.eventInquiryDtoToEvent(inquiryDto);
        assertAll(
            () -> assertEquals(TEST_EVENT_NAME, event.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, event.getSummary()),
            () -> assertEquals(TEST_EVENT_TEXT, event.getText()),
            () -> assertEquals(TEST_EVENT_DURATION_FROM, event.getDurationFrom()),
            () -> assertEquals(TEST_EVENT_DURATION_TO, event.getDurationTo()),
            () -> assertEquals(TEST_EVENT_TYPE, event.getType()),
            () -> assertEquals(TEST_EVENT_IMAGE, event.getImageUrl())
        );
    }

    @Test
    public void givenNothing_whenMapSimpleEventDtoToEntity_thenDtoHasAllProperties() {
        SimpleEventDto simpleEventDto = eventMapper.eventToSimpleEventDto(event);
        assertAll(
            () -> assertEquals(ID, simpleEventDto.getId()),
            () -> assertEquals(TEST_EVENT_NAME, simpleEventDto.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, simpleEventDto.getSummary()),
            () -> assertEquals(TEST_EVENT_TYPE, simpleEventDto.getType()),
            () -> assertEquals(TEST_EVENT_DURATION_FROM, simpleEventDto.getDurationFrom()),
            () -> assertEquals(TEST_EVENT_DURATION_TO, simpleEventDto.getDurationTo()),
            () -> assertEquals(TEST_EVENT_SOLD_SEATS, simpleEventDto.getSoldSeats())
        );
    }

    @Test
    public void givenNothing_whenMapEventInquiryDtoToEventAndBack_thenPropertiesStayTheSame() {
        EventInquiryDto originalDto = new EventInquiryDto();
        originalDto.setName(TEST_EVENT_NAME);
        originalDto.setSummary(TEST_EVENT_SUMMARY);
        originalDto.setText(TEST_EVENT_TEXT);
        originalDto.setDurationFrom(TEST_EVENT_DURATION_FROM);
        originalDto.setDurationTo(TEST_EVENT_DURATION_TO);
        originalDto.setType(TEST_EVENT_TYPE);
        originalDto.setImageUrl(TEST_EVENT_IMAGE);

        Event event = eventMapper.eventInquiryDtoToEvent(originalDto);
        EventInquiryDto mappedBackDto = eventMapper.eventToEventInquiryDto(event);

        assertAll(
            () -> assertEquals(originalDto.getName(), mappedBackDto.getName()),
            () -> assertEquals(originalDto.getSummary(), mappedBackDto.getSummary()),
            () -> assertEquals(originalDto.getText(), mappedBackDto.getText()),
            () -> assertEquals(originalDto.getDurationFrom(), mappedBackDto.getDurationFrom()),
            () -> assertEquals(originalDto.getDurationTo(), mappedBackDto.getDurationTo()),
            () -> assertEquals(originalDto.getType(), mappedBackDto.getType()),
            () -> assertEquals(originalDto.getImageUrl(), mappedBackDto.getImageUrl())
        );
    }
}
