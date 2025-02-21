package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ArtistMappingTest implements TestData {

    private final Artist artist = Artist.ArtistBuilder.anArtist()
        .withId(ID)
        .withName(TEST_ARTIST_NAME)
        .withSummary(TEST_ARTIST_SUMMARY)
        .withText(TEST_ARTIST_TEXT)
        .withImageUrl(TEST_ARTIST_IMAGE)
        .build();

    private final ArtistInquiryDto artistInquiryDto = ArtistInquiryDto.ArtistInquiryDtoBuilder.anArtistInquiryDto()
        .withName(TEST_ARTIST_NAME)
        .withSummary(TEST_ARTIST_SUMMARY)
        .withText(TEST_ARTIST_TEXT)
        .build();

    private final DetailedArtistDto detailedArtistDto = DetailedArtistDto.DetailedArtistDtoBuilder.aDetailedArtistDto()
        .withId(ID)
        .withName(TEST_ARTIST_NAME)
        .withSummary(TEST_ARTIST_SUMMARY)
        .withText(TEST_ARTIST_TEXT)
        .withImageUrl(TEST_ARTIST_IMAGE)
        .build();

    @Autowired
    private ArtistMapper artistMapper;

    @Test
    public void givenNothing_whenMapDetailedArtistDtoToEntity_thenEntityHasAllProperties() {
        DetailedArtistDto detailedArtistDto = artistMapper.artistToDetailedArtistDto(artist);
        assertAll(
            () -> assertEquals(ID, detailedArtistDto.getId()),
            () -> assertEquals(TEST_ARTIST_NAME, detailedArtistDto.getName()),
            () -> assertEquals(TEST_ARTIST_SUMMARY, detailedArtistDto.getSummary()),
            () -> assertEquals(TEST_ARTIST_TEXT, detailedArtistDto.getText()),
            () -> assertEquals(TEST_ARTIST_IMAGE, detailedArtistDto.getImageUrl())
        );
    }

    @Test
    public void givenNothing_whenMapListWithTwoArtistEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Artist> artists = new ArrayList<>();
        artists.add(artist);
        artists.add(artist);

        List<SimpleArtistDto> simpleArtistDtos = artistMapper.artistToSimpleArtistDto(artists);
        assertEquals(2, simpleArtistDtos.size());
        SimpleArtistDto simpleArtistDto = simpleArtistDtos.get(0);
        assertAll(
            () -> assertEquals(ID, simpleArtistDto.getId()),
            () -> assertEquals(TEST_ARTIST_NAME, simpleArtistDto.getName()),
            () -> assertEquals(TEST_ARTIST_SUMMARY, simpleArtistDto.getSummary())
        );
    }

    @Test
    public void givenArtist_whenMapToSimpleArtistDto_thenDtoHasAllProperties() {
        SimpleArtistDto simpleArtistDto = artistMapper.artistToSimpleArtistDto(artist);
        assertAll(
            () -> assertEquals(ID, simpleArtistDto.getId()),
            () -> assertEquals(TEST_ARTIST_NAME, simpleArtistDto.getName()),
            () -> assertEquals(TEST_ARTIST_SUMMARY, simpleArtistDto.getSummary())
        );
    }

    @Test
    public void givenDetailedArtistDto_whenMapToEntity_thenEntityHasAllProperties() {
        Artist mappedArtist = artistMapper.detailedArtistDtoToArtist(detailedArtistDto);
        assertAll(
            () -> assertEquals(ID, mappedArtist.getId()),
            () -> assertEquals(TEST_ARTIST_NAME, mappedArtist.getName()),
            () -> assertEquals(TEST_ARTIST_SUMMARY, mappedArtist.getSummary()),
            () -> assertEquals(TEST_ARTIST_TEXT, mappedArtist.getText()),
            () -> assertEquals(TEST_ARTIST_IMAGE, mappedArtist.getImageUrl())
        );
    }

    @Test
    public void givenArtistInquiryDto_whenMapToEntity_thenEntityHasAllProperties() {
        Artist mappedArtist = artistMapper.artistInquiryDtoToArtist(artistInquiryDto);
        assertAll(
            () -> assertEquals(TEST_ARTIST_NAME, mappedArtist.getName()),
            () -> assertEquals(TEST_ARTIST_SUMMARY, mappedArtist.getSummary()),
            () -> assertEquals(TEST_ARTIST_TEXT, mappedArtist.getText())
        );
    }


    @Test
    public void givenNullDto_whenMapToEntity_thenReturnsNull() {
        assertAll(
            () -> assertNull(artistMapper.detailedArtistDtoToArtist(null)),
            () -> assertNull(artistMapper.artistInquiryDtoToArtist(null))
        );
    }

    @Test
    public void givenNullList_whenMapToSimpleArtistDtoList_thenReturnsNull() {
        assertNull(artistMapper.artistToSimpleArtistDto((List<Artist>) null));
    }

    @Test
    public void givenEmptyList_whenMapToSimpleArtistDtoList_thenReturnsEmptyList() {
        List<SimpleArtistDto> result = artistMapper.artistToSimpleArtistDto(new ArrayList<>());
        assertTrue(result.isEmpty());
    }
}
