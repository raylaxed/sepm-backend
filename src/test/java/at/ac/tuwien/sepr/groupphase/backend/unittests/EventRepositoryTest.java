package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest implements TestData {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void givenNothing_whenSaveEvent_thenFindListWithOneElementAndFindEventById() {
        Event event = Event.EventBuilder.anEvent()
            .withName(TEST_EVENT_NAME)
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withType(TEST_EVENT_TYPE)
            .withImageUrl(TEST_EVENT_IMAGE)
            .withSoldSeats(TEST_EVENT_SOLD_SEATS)
            .build();

        eventRepository.save(event);

        assertAll(
            () -> assertEquals(1, eventRepository.findAll().size()),
            () -> assertNotNull(eventRepository.findById(event.getId()))
        );
    }

    @Test
    public void givenEvent_whenSearchByName_thenFindEvent() {
        Event event = Event.EventBuilder.anEvent()
            .withName("Rock Concert")
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withType(TEST_EVENT_TYPE)
            .withSoldSeats(TEST_EVENT_SOLD_SEATS)
            .build();
        eventRepository.save(event);

        assertAll(
            () -> assertEquals(1, eventRepository.findByFilters("rock", null, null, null).size()),
            () -> assertEquals(1, eventRepository.findByFilters("CONCERT", null, null, null).size()),
            () -> assertEquals(0, eventRepository.findByFilters("jazz", null,null, null).size())
        );
    }

    @Test
    public void givenMultipleEvents_whenSearchByType_thenFindMatchingEvents() {
        Event event1 = Event.EventBuilder.anEvent()
            .withName("Event 1")
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withType("Concert")
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withSoldSeats(0)
            .build();
        Event event2 = Event.EventBuilder.anEvent()
            .withName("Event 2")
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withType("Concert")
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withSoldSeats(0)
            .build();
        Event event3 = Event.EventBuilder.anEvent()
            .withName("Event 3")
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withType("Theater")
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withSoldSeats(0)
            .build();

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        assertAll(
            () -> assertEquals(2, eventRepository.findByFilters(null, "Concert", null, null).size()),
            () -> assertEquals(1, eventRepository.findByFilters(null, "Theater", null, null).size()),
            () -> assertEquals(0, eventRepository.findByFilters(null, "Opera", null, null).size())
        );
    }

    @Test
    public void givenEvents_whenFindTop10BySoldSeats_thenReturnOrderedList() {
        // Create events with different sold seats
        for (int i = 1; i <= 12; i++) {
            Event event = Event.EventBuilder.anEvent()
                .withName("Event " + i)
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withType(TEST_EVENT_TYPE)
                .withSoldSeats(i * 10)
                .build();
            eventRepository.save(event);
        }

        List<Event> topEvents = eventRepository.findTop10ByOrderBySoldSeatsDesc();
        assertAll(
            () -> assertEquals(10, topEvents.size()),
            () -> assertEquals(120, topEvents.get(0).getSoldSeats()),
            () -> assertEquals(110, topEvents.get(1).getSoldSeats()),
            () -> assertTrue(topEvents.get(0).getSoldSeats() > topEvents.get(9).getSoldSeats())
        );
    }

    @Test
    public void givenEvent_whenUpdate_thenFindUpdatedEvent() {
        Event event = Event.EventBuilder.anEvent()
            .withName(TEST_EVENT_NAME)
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withType(TEST_EVENT_TYPE)
            .withSoldSeats(0)
            .build();
        event = eventRepository.save(event);

        String newName = "Updated Event Name";
        event.setName(newName);
        eventRepository.save(event);

        Event found = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals(newName, found.getName());
    }

    @Test
    public void givenEvent_whenDelete_thenEventNotFound() {
        Event event = Event.EventBuilder.anEvent()
            .withName(TEST_EVENT_NAME)
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withType(TEST_EVENT_TYPE)
            .withSoldSeats(0)
            .build();
        event = eventRepository.save(event);

        Long id = event.getId();
        eventRepository.deleteById(id);

        assertTrue(eventRepository.findById(id).isEmpty());
    }
}
