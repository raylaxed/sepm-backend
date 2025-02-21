package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HallEndpointTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private static final String HALL_BASE_URI = "/api/v1/halls";
    private static final String ADMIN_USER = "admin@email.com";
    private static final List<String> ADMIN_ROLES = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

    @BeforeEach
    public void beforeEach() {
        hallRepository.deleteAll();
    }

    private HallInquiryDto createValidHallInquiryDto() {
        StageDto stageDto = new StageDto(
            null,  // ID will be generated
            100,   // positionX
            50,    // positionY
            200,   // width
            100    // height
        );

        SectorDto sectorDto = new SectorDto(
            null,  // ID will be generated
            1L,    // sectorName
            10,    // rows
            10,    // columns
            50L,   // price
            List.of()  // empty seats list for creation
        );

        return HallInquiryDto.HallInquiryDtoBuilder.aHallInquiryDto()
            .withName("Test Hall")
            .withCapacity(1000)
            .withCanvasWidth(1000)
            .withCanvasHeight(800)
            .withStage(stageDto)
            .withSectors(List.of(sectorDto))
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(HALL_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<HallDto> hallDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            HallDto[].class));

        assertEquals(0, hallDtos.size());
    }

    @Test
    public void givenInvalidHallDto_whenCreate_thenUnprocessableEntity() throws Exception {
        HallInquiryDto invalidHall = HallInquiryDto.HallInquiryDtoBuilder.aHallInquiryDto()
            .withCanvasWidth(null) // Violates @NotNull
            .withCanvasHeight(-1)  // Violates @Min(1)
            .build();

        String body = objectMapper.writeValueAsString(invalidHall);

        MvcResult mvcResult = this.mockMvc.perform(post(HALL_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenValidHall_whenCreate_thenCreated() throws Exception {
        // Arrange
        HallInquiryDto validHall = createValidHallInquiryDto();
        String requestBody = objectMapper.writeValueAsString(validHall);
        LOGGER.debug("Request body: {}", requestBody);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post(HALL_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        // Assert
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String responseBody = response.getContentAsString();
        LOGGER.debug("Response body: {}", responseBody);

        HallDto createdHall = objectMapper.readValue(responseBody, HallDto.class);

        assertAll(
            () -> assertNotNull(createdHall, "Created hall should not be null"),
            () -> assertNotNull(createdHall.id(), "Created hall ID should not be null"),
            () -> assertEquals(validHall.getCanvasWidth(), createdHall.canvasWidth(), "Canvas width should match"),
            () -> assertEquals(validHall.getCanvasHeight(), createdHall.canvasHeight(), "Canvas height should match"),
            () -> assertNotNull(createdHall.stage(), "Stage should not be null"),
            () -> assertEquals(validHall.getStage().positionX(), createdHall.stage().positionX(), "Stage X position should match"),
            () -> assertEquals(validHall.getStage().positionY(), createdHall.stage().positionY(), "Stage Y position should match"),
            () -> assertNotNull(createdHall.sectors(), "Sectors should not be null"),
            () -> assertEquals(1, createdHall.sectors().size(), "Should have one sector"),
            () -> assertNotNull(createdHall.sectors().get(0).id(), "Sector ID should not be null")
        );
    }

    @Test
    public void givenExistingHall_whenUpdate_thenSuccess() throws Exception {
        // First create a hall
        HallInquiryDto createDto = createValidHallInquiryDto();
        String createBody = objectMapper.writeValueAsString(createDto);

        MvcResult createResult = this.mockMvc.perform(post(HALL_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBody)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andReturn();

        HallDto createdHall = objectMapper.readValue(createResult.getResponse().getContentAsString(),
            HallDto.class);

        // Then prepare update with the same ID
        HallInquiryDto updateDto = HallInquiryDto.HallInquiryDtoBuilder.aHallInquiryDto()
            .withName("Updated Hall")
            .withCapacity(1200)
            .withCanvasWidth(1200)
            .withCanvasHeight(900)
            .withStage(new StageDto(
                createdHall.stage().id(),  // Keep the existing stage ID
                150,  // New position
                75,
                250,
                120
            ))
            .withSectors(List.of(new SectorDto(
                createdHall.sectors().get(0).id(),  // Keep the existing sector ID
                2L,  // New sector name
                12,  // New dimensions
                12,
                75L,
                List.of()
            )))
            .build();

        String updateBody = objectMapper.writeValueAsString(updateDto);

        MvcResult updateResult = this.mockMvc.perform(put(HALL_BASE_URI + "/{id}", createdHall.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateBody)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.OK.value(), updateResult.getResponse().getStatus());

        HallDto updatedHall = objectMapper.readValue(updateResult.getResponse().getContentAsString(),
            HallDto.class);

        // Verify the update
        assertAll(
            () -> assertEquals(createdHall.id(), updatedHall.id()),
            () -> assertEquals(1200, updatedHall.canvasWidth()),
            () -> assertEquals(900, updatedHall.canvasHeight()),
            () -> assertNotNull(updatedHall.stage()),
            () -> assertEquals(150, updatedHall.stage().positionX()),
            () -> assertEquals(75, updatedHall.stage().positionY()),
            () -> assertEquals(1, updatedHall.sectors().size()),
            () -> assertEquals(2L, updatedHall.sectors().get(0).sectorName()),
            () -> assertEquals(12, updatedHall.sectors().get(0).rows())
        );
    }

    @Test
    public void givenNonExistingId_whenUpdate_thenNotFound() throws Exception {
        HallInquiryDto updateDto = createValidHallInquiryDto();
        String updateBody = objectMapper.writeValueAsString(updateDto);

        MvcResult updateResult = this.mockMvc.perform(put(HALL_BASE_URI + "/{id}", 999L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateBody)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), updateResult.getResponse().getStatus());
    }

    @Test
    public void givenNonExistingHall_whenGetById_thenNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(HALL_BASE_URI + "/{id}", 999)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void givenNoAuth_whenAccess_thenUnauthorized() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(HALL_BASE_URI))
            .andDo(print())
            .andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), mvcResult.getResponse().getStatus());
    }
}