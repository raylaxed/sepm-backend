package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;

@Profile("generateData")
@Component
public class ArtistDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistDataGenerator.class);
    private static final int NUMBER_OF_ARTISTS_TO_GENERATE = 50;

    private static final String[] FEMALE_NAMES = {
        "Emma", "Sarah", "Lisa", "Emily", "Olivia", "Sophia", "Isabella",
        "Mia", "Charlotte", "Ava", "Maria", "Alice", "Victoria", "Diana",
        "Julia", "Laura", "Nina", "Anna", "Eva", "Rose"
    };

    private static final String[] FEMALE_IMAGES = {
        "http://localhost:8080/static/images/artist_female_1.jpg",
        "http://localhost:8080/static/images/artist_female_2.jpg",
        "http://localhost:8080/static/images/artist_female_3.jpg",
        "http://localhost:8080/static/images/artist_female_4.jpg"
    };

    private static final String[] MALE_NAMES = {
        "John", "Michael", "David", "James", "William", "Daniel", "Matthew",
        "Joseph", "Andrew", "Alexander", "Thomas", "Robert", "Richard",
        "Charles", "Paul", "Mark", "George", "Steven", "Peter", "Eric"
    };

    private static final String[] MALE_IMAGES = {
        "http://localhost:8080/static/images/artist_male_1.jpg",
        "http://localhost:8080/static/images/artist_male_2.jpg",
        "http://localhost:8080/static/images/artist_male_3.jpg",
        "http://localhost:8080/static/images/artist_male_4.jpg"
    };

    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
        "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    };

    private static final String[] BAND_NAMES = {
        "The Rolling Stones", "Led Zeppelin", "Pink Floyd", "The Beatles",
        "Queen", "AC/DC", "Metallica", "Nirvana", "Red Hot Chili Peppers",
        "Guns N' Roses", "The Who", "Eagles", "Aerosmith", "Black Sabbath",
        "Deep Purple", "The Doors", "The Police", "The Beach Boys",
        "Fleetwood Mac", "The Clash"
    };

    private static final String[] BAND_IMAGES = {
        "http://localhost:8080/static/images/band_1.jpg",
        "http://localhost:8080/static/images/band_2.jpg",
        "http://localhost:8080/static/images/band_3.jpg",
        "http://localhost:8080/static/images/band_4.jpg",
    };

    private static final String[] ARTIST_SUMMARIES = {
        "Legendary performer known for energetic live shows",
        "Innovative musician pushing boundaries in contemporary music",
        "Award-winning artist with multiple platinum records",
        "Pioneering artist in their genre",
        "Critically acclaimed performer with a unique style",
        "Versatile artist known for genre-blending performances",
        "Rising star in the music industry",
        "Influential figure in modern music",
        "Celebrated performer with a global following",
        "Groundbreaking artist known for experimental work"
    };

    private static final String[] ARTIST_DESCRIPTIONS = {
        "With decades of experience in the music industry, this artist has consistently delivered groundbreaking performances and chart-topping hits. Known for their unique style and powerful stage presence.",
        "A true innovator in the contemporary music scene, pushing the boundaries of conventional genres and creating a distinctive sound that has influenced countless other artists.",
        "Rising from humble beginnings, this artist has achieved international acclaim through dedication to their craft and a commitment to musical excellence.",
        "Combining traditional elements with modern innovation, this artist has created a unique sound that resonates with audiences worldwide.",
        "A versatile performer who seamlessly blends multiple genres, creating a distinctive sound that has earned both critical acclaim and commercial success."
    };

    @Autowired
    private ArtistRepository artistRepository;

    @PostConstruct
    private void generateArtists() {
        if (artistRepository.findAll().size() > 0) {
            LOGGER.debug("artists already generated");
            return;
        }

        Random random = new Random();
        LOGGER.debug("generating {} artist entries", NUMBER_OF_ARTISTS_TO_GENERATE);
        List<Artist> artists = new ArrayList<>();

        // Generate female artists (40%)
        for (int i = 0; i < NUMBER_OF_ARTISTS_TO_GENERATE * 0.4; i++) {
            String firstName = FEMALE_NAMES[random.nextInt(FEMALE_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String fullName = firstName + " " + lastName;
            
            Artist artist = Artist.ArtistBuilder.anArtist()
                .withName(fullName)
                .withSummary(ARTIST_SUMMARIES[random.nextInt(ARTIST_SUMMARIES.length)])
                .withText(ARTIST_DESCRIPTIONS[random.nextInt(ARTIST_DESCRIPTIONS.length)])
                .withImageUrl(FEMALE_IMAGES[random.nextInt(FEMALE_IMAGES.length)])
                .build();
            
            artists.add(artist);
        }

        // Generate male artists (40%)
        for (int i = 0; i < NUMBER_OF_ARTISTS_TO_GENERATE * 0.4; i++) {
            String firstName = MALE_NAMES[random.nextInt(MALE_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String fullName = firstName + " " + lastName;
            
            Artist artist = Artist.ArtistBuilder.anArtist()
                .withName(fullName)
                .withSummary(ARTIST_SUMMARIES[random.nextInt(ARTIST_SUMMARIES.length)])
                .withText(ARTIST_DESCRIPTIONS[random.nextInt(ARTIST_DESCRIPTIONS.length)])
                .withImageUrl(MALE_IMAGES[random.nextInt(MALE_IMAGES.length)])
                .build();
            
            artists.add(artist);
        }

        // Generate bands (20%)
        for (int i = 0; i < NUMBER_OF_ARTISTS_TO_GENERATE * 0.2; i++) {
            String bandName = BAND_NAMES[i % BAND_NAMES.length];
            
            Artist artist = Artist.ArtistBuilder.anArtist()
                .withName(bandName)
                .withSummary(ARTIST_SUMMARIES[random.nextInt(ARTIST_SUMMARIES.length)])
                .withText(ARTIST_DESCRIPTIONS[random.nextInt(ARTIST_DESCRIPTIONS.length)])
                .withImageUrl(BAND_IMAGES[random.nextInt(BAND_IMAGES.length)])
                .build();
            
            artists.add(artist);
        }

        LOGGER.debug("saving {} artists", artists.size());
        artistRepository.saveAll(artists);
    }
}