package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArtistService {

    /**
     * Find all artist entries.
     *
     * @return ordered list of all artist entries
     */
    List<Artist> findAll();

    /**
     * Find a single artist entry by id.
     *
     * @param id the id of the artist entry
     * @return the artist entry
     */
    Artist findOne(Long id);

    /**
     * Create a single artist entry.
     *
     * @param artist to create
     * @return created artist entry
     */
    Artist createArtist(Artist artist);

    /**
     * Save an image and return the URL.
     *
     * @param image to save
     * @return the URL of the saved image
     */
    String saveImage(MultipartFile image);

    /**
     * Search for artists by name.
     *
     * @param query the search term used to find artists
     * @return a list of Artist objects that match the search criteria
     */
    List<Artist> searchArtistsByName(String query);

}