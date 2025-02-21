package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class SimpleArtistService implements ArtistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;
    private final String imageDirectory;
    private final String imageBaseUrl;

    public SimpleArtistService(ArtistRepository artistRepository,
                             @Value("${app.storage.image-directory}") String imageDirectory,
                             @Value("${app.image-base-url}") String imageBaseUrl) {
        this.artistRepository = artistRepository;
        this.imageDirectory = imageDirectory;
        this.imageBaseUrl = imageBaseUrl;

    }

    @Override
    public List<Artist> findAll() {
        LOGGER.debug("Find all artists");
        return artistRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Artist findOne(Long id) {
        LOGGER.debug("Find artist with id {}", id);
        Artist artist = artistRepository.findByIdWithShows(id)
            .orElseThrow(() -> new NotFoundException(
                String.format("Could not find artist with id %s", id)));

        // Filter out past shows if any shows exist
        if (artist.getShows() != null) {
            artist.getShows().removeIf(show ->
                show.getDate().isBefore(LocalDate.now())
                    || (show.getDate().isEqual(LocalDate.now()) && show.getTime().isBefore(LocalTime.now()))
            );
        }

        return artist;
    }

    @Override
    public Artist createArtist(Artist artist) {
        LOGGER.debug("Create new artist {}", artist);
        return artistRepository.save(artist);
    }

    @Override
    public String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Uploaded image is empty");
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(imageDirectory, fileName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, image.getBytes());
            LOGGER.info("Image saved to {}", imagePath);
            return imageBaseUrl + fileName;
        } catch (IOException e) {
            LOGGER.error("Failed to save image", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public List<Artist> searchArtistsByName(String query) {
        LOGGER.debug("Searching for artists with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        return artistRepository.findByNameContainingIgnoreCase(query);
    }

}