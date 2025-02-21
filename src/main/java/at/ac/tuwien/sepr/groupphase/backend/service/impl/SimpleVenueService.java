package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SearchVenueDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.VenueService;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SimpleVenueService implements VenueService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;

    public SimpleVenueService(VenueRepository venueRepository, HallRepository hallRepository) {
        this.venueRepository = venueRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> findAll() {
        LOGGER.debug("Find all venues");
        return venueRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Venue findOne(Long id) {
        LOGGER.info("Servicelayer: find venue with id {}", id);
        LOGGER.debug("Find venue with id {}", id);
        return venueRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Could not find venue with id " + id));
    }



    @Override
    public Venue createVenue(VenueDto venueDto) throws ValidationException, ConflictException {
        LOGGER.debug("Create new venue: {}", venueDto);

        try {
            // Validate the input

            // Check for conflicts (e.g., duplicate venue)
            if (venueRepository.findAll().stream()
                .anyMatch(v -> v.getName().equals(venueDto.name())
                    && v.getStreet().equals(venueDto.street()))) {
                throw new ConflictException("Venue already exists",
                    List.of("A venue with this name and address already exists"));
            }

            Venue venue = new Venue();
            venue.setName(venueDto.name());
            venue.setStreet(venueDto.street());
            venue.setCity(venueDto.city());
            venue.setCounty(venueDto.county());
            venue.setPostalCode(venueDto.postalCode());

            if (venueDto.hallIds() != null) {
                venue.setHallIds(venueDto.hallIds());
            }

            Venue savedVenue = venueRepository.save(venue);
            LOGGER.debug("Created venue with id {}", savedVenue.getId());
            LOGGER.info("Servicelayer: created venue hall id: {}", savedVenue.getHallIds());
            return savedVenue;

        } catch (Exception e) {
            if (e instanceof ValidationException || e instanceof ConflictException) {
                throw e;
            }
            throw new FatalException("Failed to create venue: " + e.getMessage(), e);
        }
    }

    @Override
    public Venue updateVenue(Long id, VenueDto venueDto) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.debug("Update venue with id {}: {}", id, venueDto);

        try {


            Venue existingVenue = venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find venue with id " + id));

            // Check for conflicts (duplicate venue, but ignore self)
            if (venueRepository.findAll().stream()
                .anyMatch(v -> v.getName().equals(venueDto.name())
                    && v.getStreet().equals(venueDto.street())
                    && !v.getId().equals(id))) {
                throw new ConflictException("Venue already exists",
                    List.of("Another venue with this name and address already exists"));
            }

            // Update basic properties
            existingVenue.setName(venueDto.name());
            existingVenue.setStreet(venueDto.street());
            existingVenue.setCity(venueDto.city());
            existingVenue.setCounty(venueDto.county());
            existingVenue.setPostalCode(venueDto.postalCode());

            if (venueDto.hallIds() != null) {
                existingVenue.setHallIds(venueDto.hallIds());
            }

            Venue updatedVenue = venueRepository.save(existingVenue);
            LOGGER.debug("Updated venue with id {}", updatedVenue.getId());
            return updatedVenue;

        } catch (Exception e) {
            if (e instanceof ValidationException || e instanceof ConflictException
                || e instanceof NotFoundException) {
                throw e;
            }
            throw new FatalException("Failed to update venue: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteVenue(Long id) throws ValidationException, NotFoundException {
        LOGGER.debug("Delete venue with id {}", id);

        Venue venue = venueRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Could not find venue with id " + id));

        // Check if venue has any associated shows
        if (venueRepository.hasAssociatedShows(id)) {
            throw new ValidationException("Cannot delete venue",
                List.of("Venue is associated with one or more shows. Please delete all associated shows first."));
        }

        // Now we can safely delete the venue
        venueRepository.deleteById(id);
        LOGGER.debug("Successfully deleted venue with id {}", id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Venue> searchVenuesByName(String query) {
        LOGGER.debug("Searching for venues with query: {}", query);
        return venueRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public List<Hall> getHallsByVenueId(Long id) {
        LOGGER.debug("Service: Getting halls for venue with id {}", id);
        Venue venue = findOne(id);
        if (venue == null) {
            throw new NotFoundException("Could not find venue with id " + id);
        }

        List<Hall> halls = hallRepository.findAllById(venue.getHallIds());
        return halls;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> filterVenues(SearchVenueDto searchDto) {
        LOGGER.debug("Filter venues with criteria: {}", searchDto);
        return venueRepository.findByFilter(
            searchDto.getName(),
            searchDto.getStreet(),
            searchDto.getCity(),
            searchDto.getCounty(),
            searchDto.getPostalCode()
        );
    }

    @Override
    public List<String[]> findUniqueCountriesAndCities() {
        List<String> distinctCountries = venueRepository.findDistinctCountries();
        List<String> distinctCities = venueRepository.findDistinctCities();

        List<String[]> result = new ArrayList<>();
        result.add(0, distinctCountries.toArray(new String[0]));
        result.add(1, distinctCities.toArray(new String[0]));
        return result;
    }
}