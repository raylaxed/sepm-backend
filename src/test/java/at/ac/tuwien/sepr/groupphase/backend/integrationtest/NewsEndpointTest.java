package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NewsEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private News news = News.NewsBuilder.aNews()
        .withTitle(TEST_NEWS_TITLE)
        .withSummary(TEST_NEWS_SUMMARY)
        .withText(TEST_NEWS_TEXT)
        .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
        .withImagePaths("")
        .withEvent(null)
        .build();

    @BeforeEach
    public void beforeEach() {
        newsRepository.deleteAll();
        news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withSummary(TEST_NEWS_SUMMARY)
            .withText(TEST_NEWS_TEXT)
            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
            .withImagePaths("")
            .withEvent(null)
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<SimpleNewsDto> simpleNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleNewsDto[].class));

        assertEquals(0, simpleNewsDtos.size());
    }

    @Test
    public void givenOneMessage_whenFindAll_thenListWithSizeOneAndMessageWithAllPropertiesExceptSummary()
        throws Exception {
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<SimpleNewsDto> simpleNewsDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            SimpleNewsDto[].class));

        assertEquals(1, simpleNewsDtos.size());
        SimpleNewsDto simpleNewsDto = simpleNewsDtos.getFirst();
        assertAll(
            () -> assertEquals(news.getId(), simpleNewsDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, simpleNewsDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SUMMARY, simpleNewsDto.getSummary()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, simpleNewsDto.getPublishedAt())
        );
    }

    @Test
    public void givenOneMessage_whenFindById_thenMessageWithAllProperties() throws Exception {
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", news.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        DetailedNewsDto detailedNewsDto = objectMapper.readValue(response.getContentAsString(),
            DetailedNewsDto.class);

        assertEquals(news, newsMapper.detailedNewsDtoToNews(detailedNewsDto));
    }

    @Test
    public void givenOneMessage_whenFindByNonExistingId_then404() throws Exception {
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", -1)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenPost_thenMessageWithAllSetPropertiesPlusIdAndPublishedDate() throws Exception {
        news.setPublishedAt(null);
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String dtoJson = objectMapper.writeValueAsString(newsInquiryDto);

        MockMultipartFile newsDtoPart = new MockMultipartFile(
            "newsDto",
            "newsDto.json",
            MediaType.APPLICATION_JSON_VALUE,
            dtoJson.getBytes()
        );

        MockMultipartFile imagePart = new MockMultipartFile(
            "images",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake image content".getBytes()
        );

        MvcResult mvcResult = this.mockMvc.perform(multipart(NEWS_BASE_URI)
                .file(newsDtoPart)
                .file(imagePart)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        DetailedNewsDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            DetailedNewsDto.class);

        assertNotNull(messageResponse.getId());
        assertNotNull(messageResponse.getPublishedAt());
        assertTrue(isNow(messageResponse.getPublishedAt()));
        //Set generated properties to null to make the response comparable with the original input
        messageResponse.setId(null);
        messageResponse.setPublishedAt(null);
        News newsResponse = newsMapper.detailedNewsDtoToNews(messageResponse);
        assertEquals(news, newsResponse);
    }

    @Test
    public void givenNothing_whenPostInvalid_then422() throws Exception {
        news.setTitle(null);
        news.setSummary(null);
        news.setText(null);
        NewsInquiryDto newsInquiryDto = newsMapper.newsToNewsInquiryDto(news);
        String dtoJson = objectMapper.writeValueAsString(newsInquiryDto);

        MockMultipartFile newsDtoPart = new MockMultipartFile(
            "newsDto",
            "newsDto.json",
            MediaType.APPLICATION_JSON_VALUE,
            dtoJson.getBytes()
        );

        MockMultipartFile imagePart = new MockMultipartFile(
            "images",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake image content".getBytes()
        );

        MvcResult mvcResult = this.mockMvc.perform(multipart(NEWS_BASE_URI)
                .file(newsDtoPart)
                .file(imagePart)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(3, errors.length);
            }
        );
    }

    private boolean isNow(LocalDateTime date) {
        LocalDateTime today = LocalDateTime.now();
        return date.getYear() == today.getYear() && date.getDayOfYear() == today.getDayOfYear() &&
            date.getHour() == today.getHour();
    }

}
