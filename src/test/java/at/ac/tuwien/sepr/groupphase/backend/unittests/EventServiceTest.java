package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchEventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EventServiceTest implements TestData {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ShowRepository showRepository;

    private SimpleEventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new SimpleEventService(eventRepository, showRepository, "/images", "/images/");
    }

    @Test
    void createEvent_ValidEvent_ReturnsCreatedEvent() {
        Event event = Event.EventBuilder.anEvent()
            .withName(TEST_EVENT_NAME)
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withType(TEST_EVENT_TYPE)
            .withDurationFrom(TEST_EVENT_DURATION_FROM)
            .withDurationTo(TEST_EVENT_DURATION_TO)
            .withSoldSeats(0)
            .withShows(new ArrayList<Show>())
            .build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event created = eventService.createEvent(event);

        assertAll(
            () -> assertNotNull(created),
            () -> assertEquals(TEST_EVENT_NAME, created.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, created.getSummary()),
            () -> assertEquals(TEST_EVENT_TEXT, created.getText()),
            () -> assertEquals(TEST_EVENT_TYPE, created.getType()),
            () -> verify(eventRepository, times(1)).save(any(Event.class))
        );
    }

    @Test
    void findOne_ExistingEvent_ReturnsEvent() {
        Event event = Event.EventBuilder.anEvent()
            .withId(1L)
            .withName(TEST_EVENT_NAME)
            .withSummary(TEST_EVENT_SUMMARY)
            .withText(TEST_EVENT_TEXT)
            .withType(TEST_EVENT_TYPE)
            .withDurationFrom(LocalDate.now())
            .withDurationTo(LocalDate.now().plusDays(10))
            .withSoldSeats(0)
            .withShows(new ArrayList<Show>())
            .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event found = eventService.findOne(1L);

        assertAll(
            () -> assertNotNull(found),
            () -> assertEquals(1L, found.getId()),
            () -> assertEquals(TEST_EVENT_NAME, found.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, found.getSummary()),
            () -> assertEquals(TEST_EVENT_TEXT, found.getText())
        );
    }

    @Test
    void findOne_NonExistingEvent_ThrowsNotFoundException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.findOne(1L));
    }

    @Test
    void findTop10_WithEventType_ReturnsFilteredList() {
        List<Event> events = Arrays.asList(
            Event.EventBuilder.anEvent()
                .withName("Event 1")
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withType("Concert")
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withSoldSeats(100)
                .withShows(new ArrayList<Show>())
                .build(),
            Event.EventBuilder.anEvent()
                .withName("Event 2")
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withType("Concert")
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withSoldSeats(50)
                .withShows(new ArrayList<Show>())
                .build()
        );

        when(eventRepository.findTop10ByTypeOrderBySoldSeatsDesc("Concert")).thenReturn(events);

        List<Event> result = eventService.findTop10BySoldSeats("Concert");

        assertAll(
            () -> assertEquals(2, result.size()),
            () -> assertEquals("Concert", result.get(0).getType()),
            () -> assertTrue(result.get(0).getSoldSeats() >= result.get(1).getSoldSeats()),
            () -> assertNotNull(result.get(0).getText()),
            () -> assertNotNull(result.get(0).getSummary())
        );
    }

    @Test
    void eventsByFilter_WithValidSearchDto_ReturnsFilteredEvents() {
        SearchEventDto searchDto = new SearchEventDto();
        searchDto.setName("Test");
        searchDto.setType("Concert");

        List<Event> events = Arrays.asList(
            Event.EventBuilder.anEvent()
                .withName("Test Event")
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withType("Concert")
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withSoldSeats(0)
                .withShows(new ArrayList<Show>())
                .build()
        );

        when(eventRepository.findByFilters("Test", "Concert", null, null)).thenReturn(events);

        List<Event> result = eventService.eventsByFilter(searchDto);

        assertAll(
            () -> assertNotNull(result),
            () -> assertFalse(result.isEmpty()),
            () -> assertEquals("Test Event", result.get(0).getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, result.get(0).getSummary()),
            () -> assertEquals(TEST_EVENT_TEXT, result.get(0).getText()),
            () -> assertEquals(TEST_EVENT_DURATION_FROM, result.get(0).getDurationFrom()),
            () -> assertEquals(TEST_EVENT_DURATION_TO, result.get(0).getDurationTo())
        );
    }
}