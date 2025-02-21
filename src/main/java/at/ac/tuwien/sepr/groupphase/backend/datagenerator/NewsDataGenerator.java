package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Profile("generateData")
@DependsOn("showDataGenerator")
@Component
public class NewsDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_NEWS_TO_GENERATE = 20;
    private static final String[] NEWS_TITLES = {
        "Exciting Performance Announced",
        "Show Extended Due to Popular Demand",
        "Special Guest Star Joins Cast",
        "Behind the Scenes Look",
        "Opening Night Success",
        "Limited Time Offer on Tickets",
        "Cast Change Announcement",
        "Award Nomination",
        "Exclusive Interview with Star",
        "New Season Revealed"
    };
    private static final String[] NEWS_SUMMARIES = {
        "Don't miss this spectacular performance that critics are raving about",
        "Additional dates added to meet overwhelming demand",
        "A surprise addition to an already stellar cast",
        "Get an exclusive peek at what happens backstage",
        "Standing ovation marks successful premiere",
        "Special promotional prices for upcoming shows",
        "Exciting changes coming to the production",
        "Recognition for outstanding achievement",
        "In-depth conversation with the leading performer",
        "Upcoming season promises excitement and innovation"
    };

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final Random random = new Random();

    public NewsDataGenerator(NewsRepository newsRepository, EventRepository eventRepository) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    private void generateNews() {
        if (newsRepository.findAll().size() > 0) {
            LOGGER.debug("news already generated");
        } else {
            // Get all events and filter for future events
            List<Event> allEvents = eventRepository.findAll();
            List<Event> futureEvents = allEvents.stream()
                .filter(event -> event.getDurationFrom().isAfter(LocalDate.now()))
                .toList();

            if (futureEvents.isEmpty()) {
                LOGGER.warn("No future events found in database. Cannot generate news.");
                return;
            }

            LOGGER.debug("generating {} news entries for {} future events",
                NUMBER_OF_NEWS_TO_GENERATE, futureEvents.size());
            List<News> newsToSave = new ArrayList<>();

            for (int i = 0; i < NUMBER_OF_NEWS_TO_GENERATE; i++) {
                Event randomEvent = futureEvents.get(random.nextInt(futureEvents.size()));
                int titleIndex = i % NEWS_TITLES.length;
                int summaryIndex = i % NEWS_SUMMARIES.length;

                // Calculate a random date between now and the event date
                LocalDate eventDate = randomEvent.getDurationFrom();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime eventDateTime = eventDate.atStartOfDay();  // Convert LocalDate to LocalDateTime
                long daysBetween = now.until(eventDateTime, java.time.temporal.ChronoUnit.DAYS);
                long randomDays = daysBetween > 0 ? random.nextLong(daysBetween) : 0;
                LocalDateTime newsDate = now.plusDays(randomDays);

                News news = News.NewsBuilder.aNews()
                    .withTitle(NEWS_TITLES[titleIndex] + " - " + randomEvent.getName())
                    .withSummary(NEWS_SUMMARIES[summaryIndex])
                    .withText("Detailed information about " + randomEvent.getName() + ": "
                        + NEWS_SUMMARIES[summaryIndex] + " This is an extended description of the news article "
                        + "providing more details about the event and its significance. Event date: "
                        + eventDate)
                    .withPublishedAt(newsDate)
                    .withEvent(randomEvent)
                    .build();

                LOGGER.debug("created news {} for event on {}", news, eventDate);
                newsToSave.add(news);
            }

            LOGGER.debug("saving all {} news entries at once", newsToSave.size());
            newsRepository.saveAll(newsToSave);
        }
    }
}
