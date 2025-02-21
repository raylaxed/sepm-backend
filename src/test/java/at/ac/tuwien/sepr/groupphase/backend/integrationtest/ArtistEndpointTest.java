package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShowRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ArtistEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ShowRepository showRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private static final String DEFAULT_USER = "user";
    private static final List<String> USER_ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    @BeforeEach
    public void beforeEach() {
        showRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    public void givenValidData_whenPost_thenArtistWithAllSetPropertiesPlusId() throws Exception {
        ArtistInquiryDto artistDto = ArtistInquiryDto.ArtistInquiryDtoBuilder.anArtistInquiryDto()
            .withName(TEST_ARTIST_NAME)
            .withSummary(TEST_ARTIST_SUMMARY)
            .withText(TEST_ARTIST_TEXT)
            .build();

        String artistJson = objectMapper.writeValueAsString(artistDto);
        MockMultipartFile artistPart = new MockMultipartFile("artist", "artist.json", "application/json", artistJson.getBytes());

        ClassPathResource classPathResource = new ClassPathResource(TEST_ARTIST_IMAGE);
        byte[] imageBytes = Files.readAllBytes(classPathResource.getFile().toPath());
        MockMultipartFile image = new MockMultipartFile("image", "test_image.png", "image/png", imageBytes);


        MvcResult mvcResult = this.mockMvc.perform(multipart(ARTIST_BASE_URI)
                .file(artistPart)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DetailedArtistDto artistResponse = objectMapper.readValue(response.getContentAsString(), DetailedArtistDto.class);
        assertNotNull(artistResponse.getId());
        assertAll("Artist Response Validation",
            () -> assertEquals(TEST_ARTIST_NAME, artistResponse.getName(), "Name mismatch"),
            () -> assertEquals(TEST_ARTIST_SUMMARY, artistResponse.getSummary(), "Summary mismatch"),
            () -> assertEquals(TEST_ARTIST_TEXT, artistResponse.getText(), "Text mismatch"),
            () -> assertNotNull(artistResponse.getImageUrl(), "Image URL is null")
        );
    }

    @Test
    public void givenInvalidData_whenPost_thenReturn422WithValidationErrors() throws Exception {
        ArtistInquiryDto artistDto = ArtistInquiryDto.ArtistInquiryDtoBuilder.anArtistInquiryDto()
            .withName("")
            .withSummary("")
            .withText("   ")
            .withImageUrl("")
            .build();


        String artistJson = objectMapper.writeValueAsString(artistDto);
        MockMultipartFile artistPart = new MockMultipartFile("artist", "artist.json", "application/json", artistJson.getBytes());

        MvcResult mvcResult = this.mockMvc.perform(multipart(ARTIST_BASE_URI)
                .file(artistPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // Parse the response to check for individual validation error messages
        String responseBody = response.getContentAsString();
        assertAll("Validation Error Messages",
            () -> assertTrue(responseBody.contains("Name is required")),
            () -> assertTrue(responseBody.contains("Summary is required")),
            () -> assertTrue(responseBody.contains("Description is required"))
        );
    }

    @Test
    public void findAll_whenCalled_returnsAllArtists() throws Exception {
        // Arrange
        Artist artist1 = Artist.ArtistBuilder.anArtist()
            .withName("Test Artist 1")
            .withSummary("Test Summary 1")
            .withText("Test Text 1")
            .build();

        Artist artist2 = Artist.ArtistBuilder.anArtist()
            .withName("Test Artist 2")
            .withSummary("Test Summary 2")
            .withText("Test Text 2")
            .build();

        artist1 = artistRepository.save(artist1);
        artist2 = artistRepository.save(artist2);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/artists")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(artist1.getId()))
            .andExpect(jsonPath("$[0].name").value("Test Artist 1"))
            .andExpect(jsonPath("$[1].id").value(artist2.getId()))
            .andExpect(jsonPath("$[1].name").value("Test Artist 2"))
            .andReturn();
    }

    @Test
    public void find_whenValidId_returnsArtist() throws Exception {
        // Arrange
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName("Test Artist")
            .withSummary("Test Summary")
            .withText("Test Text")
            .build();

        Artist savedArtist = artistRepository.save(artist);
        Long artistId = savedArtist.getId();

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/artists/" + artistId)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(artistId))
            .andExpect(jsonPath("$.name").value("Test Artist"))
            .andExpect(jsonPath("$.summary").value("Test Summary"))
            .andExpect(jsonPath("$.text").value("Test Text"))
            .andReturn();
    }

    @Test
    public void searchArtists_whenValidQuery_returnsMatchingArtists() throws Exception {
        // Arrange
        Artist artist1 = Artist.ArtistBuilder.anArtist()
            .withName("Rock Band")
            .withSummary("Test Summary 1")
            .withText("Test Text 1")
            .build();

        Artist artist2 = Artist.ArtistBuilder.anArtist()
            .withName("Pop Singer")
            .withSummary("Test Summary 2")
            .withText("Test Text 2")
            .build();

        artistRepository.save(artist1);
        artistRepository.save(artist2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/artists/search")
                .param("search", "Rock")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Rock Band"))
            .andExpect(jsonPath("$[0].summary").value("Test Summary 1"));
    }

    @Test
    public void find_whenInvalidId_returns404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/artists/999")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void findAll_whenUnauthorized_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/artists"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    public void filterArtists_whenSearchingForRock_returnsMatchingArtists() throws Exception {
        // Create test artists
        for (int i = 1; i <= 20; i++) {
            Artist artist = Artist.ArtistBuilder.anArtist()
                .withName(i <= 10 ? "Rock Band " + i : "Pop Band " + i)
                .withSummary("Test Summary " + i)
                .withText("Test Description " + i)
                .build();
            artistRepository.save(artist);
        }

        MvcResult result = mockMvc.perform(get("/api/v1/artists/filter")
                .param("name", "Rock")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<SimpleArtistDto> foundArtists = Arrays.asList(objectMapper.readValue(
            result.getResponse().getContentAsString(), SimpleArtistDto[].class));

        assertAll(
            () -> assertTrue(foundArtists.size() > 1, "Should find multiple artists"),
            () -> assertEquals(10, foundArtists.size(), "Should find exactly 10 rock bands"),
            () -> assertTrue(foundArtists.stream()
                .allMatch(artist -> artist.getName().contains("Rock")), "All found artists should contain 'Rock' in name")
        );
    }

}

