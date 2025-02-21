package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import jakarta.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/v1/artists")
public class ArtistEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistEndpoint(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of artists without details", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleArtistDto> findAll() {
        LOGGER.info("GET /api/v1/artists");
        return artistMapper.artistToSimpleArtistDto(artistService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific artist", security = @SecurityRequirement(name = "apiKey"))
    public DetailedArtistDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/artists/{}", id);
        return artistMapper.artistToDetailedArtistDto(artistService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Create a new artist", security = @SecurityRequirement(name = "apiKey"))
    public DetailedArtistDto create(@Valid @RequestPart("artist") ArtistInquiryDto artist,
                                  @Valid @RequestPart(value = "image", required = false) MultipartFile image) {

        LOGGER.info("POST /api/v1/artists body: {}", artist);
        String imageUrl;
        if (image != null && !image.isEmpty()) {
            imageUrl = artistService.saveImage(image);
            artist.setImageUrl(imageUrl);
        }
        LOGGER.info("POST /api/v1/artists body: {}", artist);

        Artist newArtist = artistMapper.artistInquiryDtoToArtist(artist);
        Artist createdArtist = artistService.createArtist(newArtist);
        DetailedArtistDto result = artistMapper.artistToDetailedArtistDto(createdArtist);

        return result;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/search")
    @Operation(summary = "Search for artists by name", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleArtistDto> searchArtists(@RequestParam(name = "search") String query) {
        LOGGER.info("GET /api/v1/artists/search?search={}", query);
        return artistMapper.artistToSimpleArtistDto(artistService.searchArtistsByName(query));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/filter")
    @Operation(summary = "Search for artists by name", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleArtistDto> filterArtists(@RequestParam(name = "name", required = false) String query) {
        LOGGER.info("GET /api/v1/artists?filter={}", query);
        return artistMapper.artistToSimpleArtistDto(artistService.searchArtistsByName(query));
    }
}
