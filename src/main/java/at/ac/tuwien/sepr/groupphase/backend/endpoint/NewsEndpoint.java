package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.service.ImageService;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import jakarta.validation.Valid;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final ImageService imageService;
    private final NewsMapper newsMapper;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper, ImageService imageService) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
        this.imageService = imageService;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleNewsDto> findAll() {
        LOGGER.info("GET /api/v1/news");
        return newsMapper.newsToSimpleNewsDto(newsService.findAll());
    }

    @Transactional
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific news", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto find(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        return newsMapper.newsToDetailedNewsDto(newsService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new news", security = @SecurityRequirement(name = "apiKey"))
    public DetailedNewsDto create(@RequestPart("newsDto") @Valid NewsInquiryDto newsDto,
                                  @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        LOGGER.info("POST /api/v1/news body: {}", newsDto);

        News newNews = newsMapper.newsInquiryDtoToNews(newsDto);

        // Handle images
        if (images != null && images.length > 0) {
            StringBuilder imagesString = new StringBuilder();
            for (MultipartFile image : images) {
                imagesString.append(imageService.saveImageToDirectory(image)).append(",");
                System.out.println("Sent image to image service: " + image.getName());
            }
            newNews.setImagePaths(imagesString.toString());
        }

        return newsMapper.newsToDetailedNewsDto(
            newsService.publishNews(newNews));
    }

    @Transactional
    @Secured("ROLE_USER")
    @PutMapping("/toggleSeen")
    @Operation(summary = "Update the list of seen news for the user", security = @SecurityRequirement(name = "apiKey"))
    public List<Long> updateSeenNews(Authentication authentication, Integer newsId) {
        LOGGER.info("PUT /api/v1/news/seen: {}", newsId);

        String email = authentication.getName();
        return newsService.toggleSeenNews(newsId, email);
    }

    @Transactional
    @Secured("ROLE_USER")
    @GetMapping("/seen")
    @Operation(summary = "Get a list of of seen news for the user", security = @SecurityRequirement(name = "apiKey"))
    public List<Long> getSeenNews(Authentication authentication) {
        LOGGER.info("GET /api/v1/news/seen");

        String email = authentication.getName();
        return newsService.findAllSeenNews(email);
    }
}
