package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleEventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @BeforeEach
    public void beforeEach() {
        showRepository.deleteAll();
        eventRepository.deleteAll();
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
    }
    // Test: POST /events (create new event with form-data)
    @Test
    public void givenValidData_whenPost_thenEventWithAllSetPropertiesPlusId() throws Exception {
        final Show show = new Show();
        show.setName(TEST_SHOW_NAME);
        show.setSummary(TEST_SHOW_SUMMARY);
        show.setText(TEST_SHOW_TEXT);
        show.setDuration(TEST_SHOW_DURATION);
        show.setEventType(TEST_SHOW_TYPE);
        show.setDate(TEST_SHOW_DATE);
        show.setTime(TEST_SHOW_TIME);
        show.setCapacity(TEST_SHOW_CAPACITY);
        show.setSoldSeats(TEST_SHOW_SOLD_SEATS);
        showRepository.save(show);

        assertNotNull(show.getId(), "Show ID should not be null");

        EventInquiryDto eventDto = new EventInquiryDto();
        eventDto.setName(TEST_EVENT_NAME);
        eventDto.setSummary(TEST_EVENT_SUMMARY);
        eventDto.setText(TEST_EVENT_TEXT);
        eventDto.setDurationFrom(TEST_EVENT_DURATION_FROM);
        eventDto.setDurationTo(TEST_EVENT_DURATION_TO);
        eventDto.setType(TEST_EVENT_TYPE);
        eventDto.setSoldSeats(TEST_EVENT_SOLD_SEATS);
        eventDto.setShowIds(new Long[]{show.getId()});

        String eventJson = objectMapper.writeValueAsString(eventDto);
        MockMultipartFile eventPart = new MockMultipartFile("event", "event.json", "application/json", eventJson.getBytes());

        ClassPathResource classPathResource = new ClassPathResource(TEST_EVENT_IMAGE);
        byte[] imageBytes = Files.readAllBytes(classPathResource.getFile().toPath());
        MockMultipartFile image = new MockMultipartFile("image", "test_image.png", "image/png", imageBytes);
        MvcResult mvcResult = this.mockMvc.perform(multipart(EVENT_BASE_URI)
                .file(eventPart)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DetailedEventDto eventResponse = objectMapper.readValue(response.getContentAsString(), DetailedEventDto.class);
        assertAll(
            () -> assertNotNull(eventResponse.getId()),
            () -> assertEquals(TEST_EVENT_NAME, eventResponse.getName()),
            () -> assertEquals(TEST_EVENT_SUMMARY, eventResponse.getSummary()),
            () -> assertEquals(TEST_EVENT_TEXT, eventResponse.getText()),
            () -> assertEquals(TEST_EVENT_DURATION_TO, eventResponse.getDurationTo()),
            () -> assertEquals(TEST_EVENT_DURATION_FROM, eventResponse.getDurationFrom()),
            () -> assertEquals(TEST_EVENT_TYPE, eventResponse.getType()),
            () -> assertEquals(TEST_EVENT_SOLD_SEATS, eventResponse.getSoldSeats()),
            () -> assertNotNull(eventResponse.getImageUrl())
        );
    }

    @Test
    public void givenNoEvents_whenGetTop10_thenEmptyList() throws Exception {
        eventRepository.deleteAll();
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/top-ten")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(0, events.size());
    }

    @Test
    public void givenFiveEvents_whenGetTop10_thenReturnAllFiveOrderedBySoldSeats() throws Exception {
        eventRepository.deleteAll();

        // Create and save 5 events with different sold seats
        for (int i = 1; i <= 5; i++) {
            Event event = Event.EventBuilder.anEvent()
                .withName(TEST_EVENT_NAME + i)
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withType(TEST_EVENT_TYPE)
                .withImageUrl(TEST_EVENT_IMAGE)
                .withSoldSeats(i * 10)
                .build();
            eventRepository.save(event);
        }

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/top-ten")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(5, events.size());
        // Verify descending order
        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(events.get(i).getSoldSeats() >= events.get(i + 1).getSoldSeats());
        }
    }

    @Test
    public void given15Events_whenGetTop10_thenReturnTop10OrderedBySoldSeats() throws Exception {
        // Create and save 15 events with different sold seats
        for (int i = 1; i <= 15; i++) {
            Event event = Event.EventBuilder.anEvent()
                .withName(TEST_EVENT_NAME + i)
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withType(TEST_EVENT_TYPE)
                .withImageUrl(TEST_EVENT_IMAGE)
                .withSoldSeats(i * 10) // Different number of sold seats
                .build();
            eventRepository.save(event);
        }

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/top-ten")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(10, events.size());
        // Verify descending order and that we got the top 10
        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(events.get(i).getSoldSeats() >= events.get(i + 1).getSoldSeats());
        }
        // Verify we got the highest sold seats (should start at 150 and end at 60)
        assertEquals(150, events.get(0).getSoldSeats());
        assertEquals(60, events.get(9).getSoldSeats());
    }

    @Test
    public void givenEventsWithEqualSoldSeats_whenGetTop10_thenReturnOrderedList() throws Exception {
        // Create and save events with some equal sold seats
        for (int i = 1; i <= 13; i++) {
            Event event = Event.EventBuilder.anEvent()
                .withName(TEST_EVENT_NAME + i)
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withType(TEST_EVENT_TYPE)
                .withImageUrl(TEST_EVENT_IMAGE)
                .withSoldSeats(100) // Same number of sold seats
                .build();
            eventRepository.save(event);
        }

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/top-ten")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(10, events.size());
        // Verify all have same sold seats
        for (SimpleEventDto event : events) {
            assertEquals(100, event.getSoldSeats());
        }
    }

    @Test
    public void given15Events_whenFilterApplied_thenReturnTheMatchingEvents() throws Exception {
        for (int i = 1; i <= 15; i++) {
            Event event = Event.EventBuilder.anEvent()
                .withName(TEST_EVENT_NAME + i)
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT + i)
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withType(TEST_EVENT_TYPE + (i % 3))
                .withImageUrl(TEST_EVENT_IMAGE)
                .withSoldSeats(10)
                .build();
            eventRepository.save(event);
        }
        String filterName = "am";
        String filterType = "1";
        String filterText = "text";

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/filter")
                .param("name", filterName)
                .param("type", filterType)
                .param("text", filterText)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(5, events.size());
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        eventRepository.deleteAll();
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(0, events.size());
    }

    @Test
    public void givenMultipleEvents_whenFindAll_thenReturnAllEvents() throws Exception {
        eventRepository.deleteAll();
        // Create and save multiple events
        for (int i = 1; i <= 5; i++) {
            Event event = Event.EventBuilder.anEvent()
                .withName(TEST_EVENT_NAME + i)
                .withSummary(TEST_EVENT_SUMMARY)
                .withText(TEST_EVENT_TEXT)
                .withDurationFrom(TEST_EVENT_DURATION_FROM)
                .withDurationTo(TEST_EVENT_DURATION_TO)
                .withType(TEST_EVENT_TYPE)
                .withImageUrl(TEST_EVENT_IMAGE)
                .withSoldSeats(i * 10)
                .build();
            eventRepository.save(event);
        }

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<SimpleEventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleEventDto[].class));
        assertEquals(5, events.size());
    }

    @Test
    public void givenExistingEvent_whenFindById_thenReturnEvent() throws Exception {
        // Create and save an event
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
        Long eventId = eventRepository.save(event).getId();

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}", eventId)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DetailedEventDto foundEvent = objectMapper.readValue(response.getContentAsString(), DetailedEventDto.class);
        assertAll(
            () -> assertEquals(eventId, foundEvent.getId()),
            () -> assertEquals(event.getName(), foundEvent.getName()),
            () -> assertEquals(event.getSummary(), foundEvent.getSummary()),
            () -> assertEquals(event.getText(), foundEvent.getText()),
            () -> assertEquals(event.getDurationFrom(), foundEvent.getDurationFrom()),
            () -> assertEquals(event.getDurationTo(), foundEvent.getDurationTo()),
            () -> assertEquals(event.getType(), foundEvent.getType()),
            () -> assertEquals(event.getSoldSeats(), foundEvent.getSoldSeats())
        );
    }

    @Test
    public void givenNonExistingEvent_whenFindById_thenReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}", 999)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

}
