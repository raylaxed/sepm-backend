package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;

import java.util.List;

public interface NewsService {

    /**
     * Find all news entries ordered by published at date (descending).
     *
     * @return ordered list of al news entries
     */
    List<News> findAll();


    /**
     * Find a single news entry by id.
     *
     * @param id the id of the news entry
     * @return the news entry
     */
    News findOne(Long id);

    /**
     * Publish a single news entry.
     *
     * @param news to publish
     * @return published news entry
     */
    News publishNews(News news);

    /**
     * Returns a list of News IDs that the user has marked as seen.
     *
     * @param email address of the user
     * @return list of News IDs
     */
    List<Long> findAllSeenNews(String email);

    /**
     * Updates the list of News IDs that the user has marked as seen.
     * If the List does not exist, one will be created.
     *
     * @param id of the News entry to be marked as un/seen
     * @param email address of the user
     * @return list of News IDs
     */
    List<Long> toggleSeenNews(int id, String email);

}
