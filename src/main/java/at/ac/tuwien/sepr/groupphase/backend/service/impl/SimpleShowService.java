package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowSectorService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShowService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchShowDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.LinkedHashSet;
import java.time.LocalDateTime;



@Service
public class SimpleShowService implements ShowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShowRepository showRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;
    private final String imageDirectory;
    private final String imageBaseUrl;
    private final ShowSectorService showSectorService;
    private final TicketRepository ticketRepository;

    public SimpleShowService(ShowRepository showRepository, ArtistRepository artistRepository, VenueRepository venueRepository,
                             @Value("${app.storage.image-directory}") String imageDirectory,
                             @Value("${app.image-base-url}") String imageBaseUrl, HallRepository hallRepository,
                             ShowSectorService showSectorService, TicketRepository ticketRepository) {
        this.showRepository = showRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.hallRepository = hallRepository;
        this.imageDirectory = imageDirectory;
        this.imageBaseUrl = imageBaseUrl;
        this.showSectorService = showSectorService;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> findAll() {
        LOGGER.debug("Find all shows");
        return showRepository.findAllByOrderByDateAscTimeAsc();
    }

    @Override
    @Transactional
    public Show findOne(Long id) {
        Show show = showRepository.findByIdWithArtists(id)
            .orElseThrow(() -> new NotFoundException("Show not found"));

        // Initialize all lazy collections
        Hibernate.initialize(show.getShowSectors());

        // Initialize the venue's hallIds collection
        if (show.getVenue() != null) {
            Hibernate.initialize(show.getVenue().getHallIds());
        }

        // Initialize hall-related collections
        if (show.getHall() != null) {
            Hibernate.initialize(show.getHall().getSectors());
            Hibernate.initialize(show.getHall().getStandingSectors());
            Hibernate.initialize(show.getHall().getStage());

            // Initialize seats for each sector
            for (Sector sector : show.getHall().getSectors()) {
                Hibernate.initialize(sector.getSeats());
            }
        }

        // Initialize tickets and clean up old cart items
        Hibernate.initialize(show.getTickets());
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        // Find and delete expired cart tickets
        show.getTickets().stream()
            .filter(ticket -> ticket.getInCart() && ticket.getDate().isBefore(tenMinutesAgo))
            .forEach(ticketRepository::delete);


        show.getTickets().removeIf(ticket ->
            ticket.getInCart()
            && ticket.getDate().isBefore(tenMinutesAgo));

        return show;
    }

    @Override
    @Transactional
    public Show createShow(Show show) throws ConflictException {
        // Check for scheduling conflicts
        if (show.getVenueId() != null && show.getDate() != null && show.getTime() != null) {
            LocalDateTime showStart = LocalDateTime.of(show.getDate(), show.getTime());
            LocalDateTime showEnd = showStart.plusMinutes(show.getDuration());

            // Check venue conflicts
            List<Show> venueShows = showRepository.findByVenueId(show.getVenueId());
            for (Show existingShow : venueShows) {
                LocalDateTime existingStart = LocalDateTime.of(existingShow.getDate(), existingShow.getTime());
                LocalDateTime existingEnd = existingStart.plusMinutes(existingShow.getDuration());

                if (!(showEnd.isBefore(existingStart) || showStart.isAfter(existingEnd))) {
                    throw new ConflictException("There is already a show scheduled at this venue during the specified time",
                        List.of("There is already a show scheduled at this venue during the specified time"));
                }
            }

            // Check hall conflicts if hall is specified
            if (show.getHallId() != null) {
                List<Show> hallShows = showRepository.findByHallId(show.getHallId());
                for (Show existingShow : hallShows) {
                    LocalDateTime existingStart = LocalDateTime.of(existingShow.getDate(), existingShow.getTime());
                    LocalDateTime existingEnd = existingStart.plusMinutes(existingShow.getDuration());

                    if (!(showEnd.isBefore(existingStart) || showStart.isAfter(existingEnd))) {
                        throw new IllegalArgumentException("There is already a show scheduled in this hall during the specified time");
                    }
                }
            }
        }

        // 1. First, validate and set up the venue
        if (show.getVenueId() != null) {
            Venue venue = venueRepository.findById(show.getVenueId())
                .orElseThrow(() -> new NotFoundException("Venue not found with id: " + show.getVenueId()));
            show.setVenue(venue);
        }

        // 2. Then, validate and set up the hall
        if (show.getHallId() != null) {
            Hall hall = hallRepository.findById(show.getHallId())
                .orElseThrow(() -> new NotFoundException("Hall not found with id: " + show.getHallId()));

            // Initialize the collections
            Hibernate.initialize(hall.getSectors());
            Hibernate.initialize(hall.getStandingSectors());
            Hibernate.initialize(hall.getStage());

            show.setHall(hall);
            show.setCapacity(hall.getCapacity());
        }

        // 3. Set up artists
        Set<Artist> artists = new LinkedHashSet<>();
        if (show.getArtistIds() != null) {
            for (Long artistId : show.getArtistIds()) {
                Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new NotFoundException("Artist not found with id: " + artistId));
                artists.add(artist);
            }
        }
        show.setArtists(artists);

        // 4. Save the show first to get its ID
        Show savedShow = showRepository.save(show);

        // 5. Create and save show sectors
        if (savedShow.getShowSectors() != null && !savedShow.getShowSectors().isEmpty()) {
            List<ShowSector> processedShowSectors = showSectorService.createShowSectors(savedShow);
            savedShow.setShowSectors(processedShowSectors);
        }

        // 6. Update artist relationships
        for (Artist artist : artists) {
            artist.getShows().add(savedShow);
            artistRepository.save(artist);
        }

        // Initialize collections for the response
        if (savedShow.getHall() != null) {
            Hibernate.initialize(savedShow.getHall().getSectors());
            Hibernate.initialize(savedShow.getHall().getStandingSectors());
            Hibernate.initialize(savedShow.getHall().getStage());

            for (Sector sector : savedShow.getHall().getSectors()) {
                Hibernate.initialize(sector.getSeats());
            }
        }

        if (savedShow.getVenue() != null) {
            Hibernate.initialize(savedShow.getVenue().getHallIds());
        }

        return savedShow;
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
    @Transactional
    public List<Show> findShowsWithoutEvent(String searchQuery, LocalDate dateFrom, LocalDate dateTo) {
        LOGGER.debug("Finding shows without event with search={}, dateFrom={}, dateTo={}", searchQuery, dateFrom, dateTo);
        return showRepository.findAllByEventIsNullWithFilters(searchQuery, dateFrom, dateTo);
    }

    @Override
    public List<Show> showsByFilter(SearchShowDto searchShowDto) {
        LOGGER.debug("Find shows by filter {}", searchShowDto);

        return showRepository.findByFilters(
            searchShowDto.getName(),
            searchShowDto.getDate(),
            searchShowDto.getTimeFrom(),
            searchShowDto.getTimeTo(),
            searchShowDto.getMinPrice(),
            searchShowDto.getMaxPrice(),
            searchShowDto.getEventName(),
            searchShowDto.getVenueId(),
            searchShowDto.getType()
        );
    }

    @Transactional(readOnly = true)
    public List<Show> findAllByIds(List<Long> ids) {
        LOGGER.debug("Finding shows with ids: {}", ids);
        return showRepository.findAllByIdsWithArtists(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> findShowsByHallId(Long hallId) {
        LOGGER.debug("Find shows for hall with id {}", hallId);
        return showRepository.findByHallId(hallId);
    }

}
