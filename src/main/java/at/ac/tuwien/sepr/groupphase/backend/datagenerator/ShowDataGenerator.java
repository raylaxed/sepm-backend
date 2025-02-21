package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.lang.StringBuilder;

@Profile("generateData")
@Component
@Order(3)
@DependsOn({"venueDataGenerator", "artistDataGenerator"})
public class ShowDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowDataGenerator.class);
    private static final int NUMBER_OF_EVENTS_TO_GENERATE = 200;
    private static final int NUMBER_OF_SOLO_SHOWS = 50;
    private static final String[] EVENT_TYPES = {
        "Cinema", "Sports Match", "Theatre", "Concert", "Comedy Show",
        "Musical", "Festival", "Exhibition", "Conference", "Workshop"
    };

    // Organize names by type
    private static final Map<String, String[]> EVENT_NAMES_BY_TYPE = Map.of(
        "Cinema", new String[]{"Movie Premiere", "Film Festival", "Director's Cut", "Special Screening"},
        "Sports Match", new String[]{"Championship Finals", "League Cup", "Derby Day", "Tournament"},
        "Theatre", new String[]{"Hamlet", "Romeo and Juliet", "King Lear", "A Midsummer Night's Dream"},
        "Concert", new String[]{"Rock Revolution", "Jazz Night", "Symphony Evening", "Music Festival"},
        "Comedy Show", new String[]{"Stand-up Night", "Comedy Club", "Laugh Factory", "Jest Fest"},
        "Musical", new String[]{"Chicago", "Cats", "The Phantom", "Les Misérables"},
        "Festival", new String[]{"Summer Festival", "Arts Festival", "Cultural Festival", "Music Fest"},
        "Exhibition", new String[]{"Art Gallery", "Modern Exhibition", "Photography Show", "Sculpture Display"},
        "Conference", new String[]{"Tech Summit", "Business Conference", "Industry Meet", "Annual Convention"},
        "Workshop", new String[]{"Masterclass", "Creative Workshop", "Learning Session", "Skill Development"}
    );

    // Organize images by type
    private static final Map<String, String[]> EVENT_IMAGES_BY_TYPE = Map.of(
        "Cinema", new String[]{"http://localhost:8080/static/images/cinema_1.jpg", "http://localhost:8080/static/images/cinema_2.jpg",
            "http://localhost:8080/static/images/cinema_3.jpg"},
        "Sports Match", new String[]{"http://localhost:8080/static/images/sport_1.jpg",
            "http://localhost:8080/static/images/sport_2.jpg", "http://localhost:8080/static/images/sport_3.jpg"},
        "Theatre", new String[]{"http://localhost:8080/static/images/theatre_musical_1.jpg", "http://localhost:8080/static/images/theatre_musical_2.jpg",
            "http://localhost:8080/static/images/theatre_musical_3.jpg"},
        "Concert", new String[]{"http://localhost:8080/static/images/concert_1.jpg", "http://localhost:8080/static/images/concert_2.jpg",
            "http://localhost:8080/static/images/concert_3.jpg", "http://localhost:8080/static/images/concert_4.jpg", "http://localhost:8080/static/images/concert_5.jpg",
            "http://localhost:8080/static/images/concert_6.jpg", "http://localhost:8080/static/images/concert_7.jpg", "http://localhost:8080/static/images/concert_8.jpg",
            "http://localhost:8080/static/images/concert_9.jpg", "http://localhost:8080/static/images/concert_10.jpg", "http://localhost:8080/static/images/concert_11.jpg",
            "http://localhost:8080/static/images/concert_12.jpg"},
        "Comedy Show", new String[]{null},
        "Musical", new String[]{"http://localhost:8080/static/images/theatre_musical_1.jpg", "http://localhost:8080/static/images/theatre_musical_2.jpg",
            "http://localhost:8080/static/images/theatre_musical_3.jpg"},
        "Festival", new String[]{"http://localhost:8080/static/images/festival_1.jpg",
            "http://localhost:8080/static/images/festival_2.jpg", "http://localhost:8080/static/images/festival_3.jpg",
            "http://localhost:8080/static/images/concert_6.jpg", "http://localhost:8080/static/images/concert_7.jpg",
            "http://localhost:8080/static/images/concert_8.jpg", "http://localhost:8080/static/images/concert_9.jpg",
            "http://localhost:8080/static/images/concert_10.jpg", "http://localhost:8080/static/images/concert_11.jpg", "http://localhost:8080/static/images/concert_12.jpg"},
        "Exhibition", new String[]{"http://localhost:8080/static/images/exhibition_1.jpg",
            "http://localhost:8080/static/images/exhibition_2.jpg", "http://localhost:8080/static/images/exhibition_3.jpg"},
        "Conference", new String[]{"http://localhost:8080/static/images/conference_1.jpg",
            "http://localhost:8080/static/images/conference_2.jpg"},
        "Workshop", new String[]{"http://localhost:8080/static/images/workshop_1.jpg",
            "http://localhost:8080/static/images/workshop_2.jpg"}
    );

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private HallRepository hallRepository;

    @PostConstruct
    private void generateEvents() {
        if (eventRepository.findAll().size() > 0) {
            LOGGER.debug("events already generated");
        } else {
            generateEventsWithShows();
            generateSoloShows();
        }
    }

    private String generateRandomText(int minLength, int maxLength) {
        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder text = new StringBuilder();
        String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt"
            + " ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris "
            + "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse "
            + "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui "
            + "officia deserunt mollit anim id est laborum. ";

        while (text.length() < length) {
            text.append(lorem);
        }

        return text.substring(0, length);
    }

    private String getRandomNameForType(String eventType, Random random) {
        String[] names = EVENT_NAMES_BY_TYPE.get(eventType);
        return names[random.nextInt(names.length)];
    }

    private String getRandomImageForType(String eventType, Random random) {
        String[] images = EVENT_IMAGES_BY_TYPE.get(eventType);
        return images[random.nextInt(images.length)];
    }

    private void generateEventsWithShows() {
        LOGGER.debug("generating {} event entries", NUMBER_OF_EVENTS_TO_GENERATE);
        Random random = new Random();
        List<Hall> halls = hallRepository.findAll();
        List<Event> events = new ArrayList<>();
        List<Show> allShows = new ArrayList<>();

        // Calculate max shows per event to stay under 800 total (including solo shows)
        int maxShowsPerEvent = (800 - NUMBER_OF_SOLO_SHOWS) / NUMBER_OF_EVENTS_TO_GENERATE;

        List<Venue> venues = venueRepository.findAll();
        if (venues.isEmpty()) {
            LOGGER.error("No venues found in database");
            return;
        }

        for (int i = 0; i < NUMBER_OF_EVENTS_TO_GENERATE; i++) {
            // First select the type
            String eventType = EVENT_TYPES[random.nextInt(EVENT_TYPES.length)];

            // Get matching name and image for this type
            String eventName = getRandomNameForType(eventType, random) + " " + (i + 1);
            String imageUrl = getRandomImageForType(eventType, random);

            // Generate dates between 6 months ago and 6 months in the future
            LocalDate startDate = LocalDate.now()
                .plusDays(random.nextInt(365) - 180); // -180 to +185 days from now
            LocalDate endDate = startDate.plusDays(random.nextInt(14) + 1);

            Event event = Event.EventBuilder.anEvent()
                .withName(eventName)
                .withType(eventType)
                .withImageUrl(imageUrl)
                .withSummary(generateRandomText(250, 500))
                .withText(generateRandomText(2000, 10000))
                .withDurationFrom(startDate)
                .withDurationTo(endDate)
                .withSoldSeats(0)
                .build();

            events.add(event);

            // Create shows only within the event's duration period
            int numberOfShows = 1 + random.nextInt(Math.min(5, maxShowsPerEvent));
            List<Show> shows = new ArrayList<>();

            for (int j = 0; j < numberOfShows; j++) {
                int capacity = 50 + random.nextInt(451); // 50-500 seats

                LocalDate showDate = startDate.plusDays(random.nextInt(
                    (int) startDate.until(endDate).getDays() + 1));

                int duration = 60 + random.nextInt(121);  // 60-180 minutes

                Hall hall = halls.get(random.nextInt(halls.size()));

                Show show = Show.ShowBuilder.aShow()
                    .withName(eventName + " - Show " + (j + 1))
                    .withSummary(generateRandomText(250, 500))
                    .withText(generateRandomText(2000, 10000))
                    .withEventType(eventType)
                    .withImageUrl(EVENT_IMAGES_BY_TYPE.get(eventType)[random.nextInt(EVENT_IMAGES_BY_TYPE.get(eventType).length)])
                    .withDate(showDate)
                    .withTime(LocalTime.of(18 + random.nextInt(4), random.nextInt(4) * 15))
                    .withDuration(duration)
                    .withCapacity(capacity)
                    .withSoldSeats(0)
                    .withEvent(event)
                    .withHall(hall)
                    .build();

                // Replace artist creation with random selection from existing artists
                List<Artist> allArtists = artistRepository.findAll();
                Set<Artist> artists = new HashSet<>();
                int numArtists = 1 + random.nextInt(2);
                while (artists.size() < numArtists) {
                    Artist randomArtist = allArtists.get(random.nextInt(allArtists.size()));
                    artists.add(randomArtist);
                }
                show.setArtists(artists);

                // Select a random venue
                Venue selectedVenue = venues.get(random.nextInt(venues.size()));

                // Get halls for the selected venue
                List<Hall> venueHalls = hallRepository.findByVenueId(selectedVenue.getId());
                if (!venueHalls.isEmpty()) {
                    // Select a random hall from the venue
                    Hall selectedHall = venueHalls.get(random.nextInt(venueHalls.size()));

                    show.setVenue(selectedVenue);
                    show.setHall(selectedHall);
                    // Use hall's capacity instead of random
                    show.setCapacity(selectedHall.getCapacity());

                    // Generate random prices for each sector
                    List<ShowSector> sectorPrices = new ArrayList<>();

                    // For regular sectors
                    for (Sector sector : selectedHall.getSectors()) {
                        double rawPrice = 20.0 + random.nextDouble() * 180.0;
                        double price = Math.round(rawPrice * 100.0) / 100.0;
                        sectorPrices.add(new ShowSector(show, sector, null, price));
                    }

                    // For standing sectors
                    for (StandingSector sector : selectedHall.getStandingSectors()) {
                        double rawPrice = 15.0 + random.nextDouble() * 85.0;
                        double price = Math.round(rawPrice * 100.0) / 100.0;
                        sectorPrices.add(new ShowSector(show, null, sector, price));
                    }

                    show.setShowSectors(sectorPrices);

                    double minPrice = Double.MAX_VALUE;
                    double maxPrice = 0.0;

                    for (ShowSector sectorPrice : sectorPrices) {
                        minPrice = Math.min(minPrice, sectorPrice.getPrice());
                        maxPrice = Math.max(maxPrice, sectorPrice.getPrice());
                    }

                    show.setMinPrice(minPrice);
                    show.setMaxPrice(maxPrice);

                    shows.add(show);
                }
            }

            allShows.addAll(shows);
        }

        // Batch save all entities
        LOGGER.debug("saving {} events", events.size());
        eventRepository.saveAll(events);
        
        LOGGER.debug("saving {} shows", allShows.size());
        showRepository.saveAll(allShows);
    }

    private void generateSoloShows() {
        LOGGER.debug("generating {} solo show entries", NUMBER_OF_SOLO_SHOWS);
        Random random = new Random();
        List<Hall> halls = hallRepository.findAll();
        List<Show> soloShows = new ArrayList<>();

        List<Venue> venues = venueRepository.findAll();
        if (venues.isEmpty()) {
            LOGGER.error("No venues found in database");
            return;
        }

        for (int i = 0; i < NUMBER_OF_SOLO_SHOWS; i++) {
            String showName = getRandomNameForType("Comedy Show", random) + " " + (i + 1);
            String eventType = EVENT_TYPES[random.nextInt(EVENT_TYPES.length)];

            // Generate dates between 6 months ago and 6 months in the future
            LocalDate showDate = LocalDate.now()
                .plusDays(random.nextInt(365) - 180); // -180 to +185 days from now

            int capacity = 30 + random.nextInt(171);

            int duration = 60 + random.nextInt(121);

            Hall hall = halls.get(random.nextInt(halls.size()));

            Show show = Show.ShowBuilder.aShow()
                .withName(showName)
                .withSummary(generateRandomText(250, 500))
                .withText(generateRandomText(2000, 10000))
                .withEventType(eventType)
                .withImageUrl(EVENT_IMAGES_BY_TYPE.get(eventType)[random.nextInt(EVENT_IMAGES_BY_TYPE.get(eventType).length)])
                .withDate(showDate)
                .withTime(LocalTime.of(19 + random.nextInt(3), random.nextInt(4) * 15))
                .withDuration(duration)
                .withCapacity(capacity)
                .withSoldSeats(0)
                .withHall(hall)
                .build();

            // Select a random venue
            Venue selectedVenue = venues.get(random.nextInt(venues.size()));

            // Get halls for the selected venue
            List<Hall> venueHalls = hallRepository.findByVenueId(selectedVenue.getId());
            if (!venueHalls.isEmpty()) {
                // Select a random hall from the venue
                Hall selectedHall = venueHalls.get(random.nextInt(venueHalls.size()));

                // Use hall's capacity instead of random
                capacity = selectedHall.getCapacity();

                show.setVenue(selectedVenue);
                show.setHall(selectedHall);
                show.setCapacity(capacity);

                // Generate random prices for each sector
                List<ShowSector> sectorPrices = new ArrayList<>();

                // For regular sectors
                for (Sector sector : selectedHall.getSectors()) {
                    double rawPrice = 20.0 + random.nextDouble() * 180.0;
                    double price = Math.round(rawPrice * 100.0) / 100.0;
                    sectorPrices.add(new ShowSector(show, sector, null, price));
                }

                // For standing sectors
                for (StandingSector sector : selectedHall.getStandingSectors()) {
                    double rawPrice = 15.0 + random.nextDouble() * 85.0;
                    double price = Math.round(rawPrice * 100.0) / 100.0;
                    sectorPrices.add(new ShowSector(show, null, sector, price));
                }

                show.setShowSectors(sectorPrices);

                double minPrice = Double.MAX_VALUE;
                double maxPrice = 0.0;

                for (ShowSector sectorPrice : sectorPrices) {
                    minPrice = Math.min(minPrice, sectorPrice.getPrice());
                    maxPrice = Math.max(maxPrice, sectorPrice.getPrice());
                }

                show.setMinPrice(minPrice);
                show.setMaxPrice(maxPrice);

                soloShows.add(show);
            }
        }

        // Batch save all solo shows
        LOGGER.debug("saving {} solo shows", soloShows.size());
        showRepository.saveAll(soloShows);
    }
}