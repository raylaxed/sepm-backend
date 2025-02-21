package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Profile("generateData")
@Component
@Order(2)
public class VenueDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;

    public VenueDataGenerator(VenueRepository venueRepository, HallRepository hallRepository) {
        this.venueRepository = venueRepository;
        this.hallRepository = hallRepository;
    }

    @PostConstruct
    @Transactional
    void generateVenues() {
        LOGGER.debug("Starting venue generation...");
        List<Venue> existingVenues = venueRepository.findAll();
        LOGGER.debug("Found {} existing venues", existingVenues.size());

        if (existingVenues.size() > 0) {
            LOGGER.debug("venues already generated");
            return;
        }

        LOGGER.debug("generating venues");

        // Array of venue data
        VenueData[] venueData = {
            new VenueData("Wiener Stadthalle", "Roland Rainer Platz 1", "Vienna", "Austria", "1150"),
            new VenueData("Volkstheater", "Arthur-Schnitzler-Platz 1", "Vienna", "Austria", "1070"),
            new VenueData("Konzerthaus", "Lothringerstraße 20", "Vienna", "Austria", "1030"),
            new VenueData("Musikverein", "Musikvereinsplatz 1", "Vienna", "Austria", "1010"),
            new VenueData("Arena Wien", "Baumgasse 80", "Vienna", "Austria", "1030"),
            new VenueData("Gasometer", "Guglgasse 8", "Vienna", "Austria", "1110"),
            new VenueData("Ernst-Happel-Stadion", "Meiereistraße 7", "Vienna", "Austria", "1020"),
            new VenueData("Ronacher", "Seilerstätte 9", "Vienna", "Austria", "1010"),
            new VenueData("Raimund Theater", "Wallgasse 18-20", "Vienna", "Austria", "1060"),
            new VenueData("Theater an der Wien", "Linke Wienzeile 6", "Vienna", "Austria", "1060")
        };

        // Get all halls first
        List<Hall> allHalls = hallRepository.findAll();
        LOGGER.debug("Found {} halls to assign to venues", allHalls.size());

        int hallIndex = 0;

        // Create and save venues with their halls
        for (VenueData data : venueData) {
            Venue venue = new Venue();
            venue.setName(data.name);
            venue.setStreet(data.street);
            venue.setCity(data.city);
            venue.setCounty(data.county);
            venue.setPostalCode(data.postalCode);

            // Save venue first to get ID
            venue = venueRepository.save(venue);
            LOGGER.debug("Created venue: {} with ID: {}", venue.getName(), venue.getId());

            // Create a list to hold halls for this venue
            List<Hall> venueHalls = new ArrayList<>();

            // Assign 2-3 halls to each venue
            int hallsToAssign = Math.min(2 + (int) (Math.random() * 2), allHalls.size() - hallIndex);
            for (int i = 0; i < hallsToAssign && hallIndex < allHalls.size(); i++) {
                Hall hall = allHalls.get(hallIndex++);
                hall.setVenue(venue);
                hall = hallRepository.save(hall);
                venueHalls.add(hall);
                LOGGER.debug("Assigned hall {} to venue {}", hall.getId(), venue.getName());
            }

            // Update venue with halls
            for (Hall hall : venueHalls) {
                venue.addHallId(hall.getId());
            }
            venue = venueRepository.save(venue);
            LOGGER.debug("Updated venue {} with {} halls", venue.getName(), venue.getHallIds().size());
        }

        // Add this right after you save the venues
        List<Venue> checkVenues = venueRepository.findAll();
        LOGGER.info("VenueDataGenerator: Just finished generating {} venues with IDs: {}",
            checkVenues.size(),
            checkVenues.stream().map(Venue::getId).collect(Collectors.toList()));
    }

    // Helper class for venue data
    private static class VenueData {
        String name;
        String street;
        String city;
        String county;
        String postalCode;

        VenueData(String name, String street, String city, String county, String postalCode) {
            this.name = name;
            this.street = street;
            this.city = city;
            this.county = county;
            this.postalCode = postalCode;
        }
    }
}