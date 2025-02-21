package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.entity.SeenNews;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeenNewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final SeenNewsRepository seenNewsRepository;
    private final CustomUserDetailService customUserDetailService;

    public NewsServiceImpl(NewsRepository newsRepository,
                           CustomUserDetailService customUserDetailService,
                           SeenNewsRepository seenNewsRepository) {
        this.newsRepository = newsRepository;
        this.customUserDetailService = customUserDetailService;
        this.seenNewsRepository = seenNewsRepository;
    }

    @Override
    public List<News> findAll() {
        LOGGER.debug("Find all news");
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    @Override
    public News findOne(Long id) {
        LOGGER.debug("Find news with id {}", id);
        Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()) {
            return news.get();
        } else {
            throw new NotFoundException(String.format("Could not find news with id %s", id));
        }
    }

    @Override
    public News publishNews(News news) {
        LOGGER.debug("Publish new news {}", news);
        news.setPublishedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    @Transactional
    @Override
    public List<Long> findAllSeenNews(String email) {
        LOGGER.debug("Find all news seen for email {}", email);

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail(email);

        // Find or create SeenNews entry for the user
        SeenNews seenNews = seenNewsRepository.findByUser(user)
            .orElseGet(() -> {
                SeenNews newSeenNews = new SeenNews();
                newSeenNews.setUser(user);
                return seenNewsRepository.save(newSeenNews);
            });

        // Fetch IDs of seen news
        return seenNews.getSeenNews().stream()
            .map(News::getId).toList();
    }

    @Transactional
    @Override
    public List<Long> toggleSeenNews(int id, String email) {
        LOGGER.debug("Toggle seen news {}", id);

        ApplicationUser user = customUserDetailService.findApplicationUserByEmail(email);

        // Find or create SeenNews entry for the user
        SeenNews seenNews = seenNewsRepository.findByUser(user)
            .orElseGet(() -> {
                SeenNews newSeenNews = new SeenNews();
                newSeenNews.setUser(user);
                return seenNewsRepository.save(newSeenNews);
            });

        // Fetch IDs of seen news
        List<Long> seenNewsIds = new java.util.ArrayList<>(seenNews.getSeenNews().stream()
            .map(News::getId).toList());

        // Find the News entity for the given ID
        News news = newsRepository.findById((long) id)
            .orElseThrow(() -> new IllegalArgumentException("News with ID " + id + " not found"));

        // Toggle the seen status
        if (seenNewsIds.contains((long) id)) {
            // Remove the news entry from seenNews
            seenNews.getSeenNews().remove(news);
            LOGGER.debug("News {} removed from seen list", id);
        } else {
            // Add the news entry to seenNews
            seenNews.getSeenNews().add(news);
            LOGGER.debug("News {} added to seen list", id);
        }

        // Save the updated SeenNews entity
        seenNewsRepository.save(seenNews);

        // Return the updated list of seen news IDs
        return seenNews.getSeenNews().stream()
            .map(News::getId)
            .toList();
    }
}
