package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.entity.Show;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShowSector;
import at.ac.tuwien.sepr.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowSectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SimpleShowSectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowSectorServiceTest {

    @Mock
    private ShowSectorRepository showSectorRepository;

    private SimpleShowSectorService showSectorService;
    private Show testShow;
    private Hall testHall;
    private Sector testSector;
    private StandingSector testStandingSector;

    @BeforeEach
    void setUp() {
        showSectorService = new SimpleShowSectorService(showSectorRepository);

        // Setup test hall
        testHall = new Hall();
        testHall.setId(1L);
        testHall.setName("Test Hall");

        // Setup test sector
        testSector = new Sector();
        testSector.setId(1L);
        testSector.setHall(testHall);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(testSector);
        testHall.setSectors(sectors);

        // Setup test standing sector
        testStandingSector = new StandingSector();
        testStandingSector.setId(1L);
        testStandingSector.setHall(testHall);
        Set<StandingSector> standingSectors = new HashSet<>();
        standingSectors.add(testStandingSector);
        testHall.setStandingSectors(standingSectors);

        // Setup test show
        testShow = new Show();
        testShow.setId(1L);
        testShow.setHall(testHall);
    }

    @Test
    void createShowSector_Success() {
        ShowSector showSector = new ShowSector();
        showSector.setPrice(100.0);
        when(showSectorRepository.save(any(ShowSector.class))).thenReturn(showSector);

        ShowSector result = showSectorService.createShowSector(showSector);

        assertNotNull(result);
        assertEquals(100.0, result.getPrice());
        verify(showSectorRepository).save(showSector);
    }

    @Test
    void getShowSectorBySectorId_Found() {
        ShowSector showSector = new ShowSector();
        showSector.setId(1L);
        when(showSectorRepository.findByShowIdAndSectorId(1L, 1L))
            .thenReturn(Optional.of(showSector));

        Optional<ShowSector> result = showSectorService.getShowSectorBySectorId(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void createShowSectors_Success() {
        // Setup test show sectors
        ShowSector showSector = new ShowSector();
        showSector.setSector(testSector);
        showSector.setPrice(100.0);
        testShow.setShowSectors(Arrays.asList(showSector));

        when(showSectorRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<ShowSector> result = showSectorService.createShowSectors(testShow);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testSector, result.get(0).getSector());
        assertEquals(100.0, result.get(0).getPrice());
        verify(showSectorRepository).saveAll(any());
    }

    @Test
    void createShowSectors_WithInvalidSector_ThrowsNotFoundException() {
        // Setup show sector with non-existent sector
        ShowSector showSector = new ShowSector();
        Sector invalidSector = new Sector();
        invalidSector.setId(999L);
        showSector.setSector(invalidSector);
        testShow.setShowSectors(Arrays.asList(showSector));

        assertThrows(NotFoundException.class, () -> showSectorService.createShowSectors(testShow));
    }

    @Test
    void createShowSectors_WithNullShowSectors_ThrowsIllegalArgumentException() {
        testShow.setShowSectors(null);

        assertThrows(IllegalArgumentException.class, () -> showSectorService.createShowSectors(testShow));
    }

    @Test
    void createShowSectors_WithEmptyShowSectors_ThrowsIllegalArgumentException() {
        testShow.setShowSectors(List.of());

        assertThrows(IllegalArgumentException.class, () -> showSectorService.createShowSectors(testShow));
    }
} 