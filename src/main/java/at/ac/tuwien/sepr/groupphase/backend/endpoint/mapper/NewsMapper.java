package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper interface for converting between News entities and DTOs.
 * This mapper provides methods to convert News entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 * It also includes utility methods for handling image path conversions.
 */
@Mapper(componentModel = "spring", uses = {EventMapper.class, NewsMapper.class})
public interface NewsMapper {

    /**
     * Converts a News entity to a SimpleNewsDto.
     * This method maps basic properties and extracts the first image as preview.
     *
     * @param news the News entity to convert
     * @return the corresponding SimpleNewsDto
     */
    @Named("simpleNews")
    @Mapping(target = "previewImage", source = "imagePaths", qualifiedByName = "extractFirstImage")
    SimpleNewsDto newsToSimpleNewsDto(News news);

    /**
     * Converts a list of News entities to a list of SimpleNewsDtos.
     * Uses the named mapping "simpleNews" since SimpleNewsDto misses the text property.
     *
     * @param news the list of News entities to convert
     * @return the corresponding list of SimpleNewsDtos
     */
    @IterableMapping(qualifiedByName = "simpleNews")
    List<SimpleNewsDto> newsToSimpleNewsDto(List<News> news);

    /**
     * Converts a News entity to a DetailedNewsDto.
     * This method includes all news properties and splits image paths into an array.
     *
     * @param news the News entity to convert
     * @return the corresponding DetailedNewsDto
     */
    @Mapping(source = "imagePaths", target = "imagePaths", qualifiedByName = "splitImagePaths")
    @Mapping(source = "event", target = "event")
    DetailedNewsDto newsToDetailedNewsDto(News news);

    /**
     * Converts a DetailedNewsDto to a News entity.
     * This method concatenates the image paths array into a single string.
     *
     * @param detailedNewsDto the DetailedNewsDto to convert
     * @return the corresponding News entity
     */
    @Mapping(source = "imagePaths", target = "imagePaths", qualifiedByName = "concatImagePaths")
    @Mapping(source = "event", target = "event")
    News detailedNewsDtoToNews(DetailedNewsDto detailedNewsDto);

    /**
     * Converts a NewsInquiryDto to a News entity.
     *
     * @param newsInquiryDto the NewsInquiryDto to convert
     * @return the corresponding News entity
     */
    @Mapping(target = "event", source = "event")
    News newsInquiryDtoToNews(NewsInquiryDto newsInquiryDto);

    /**
     * Converts a News entity to a NewsInquiryDto.
     *
     * @param news the News entity to convert
     * @return the corresponding NewsInquiryDto
     */
    NewsInquiryDto newsToNewsInquiryDto(News news);

    /**
     * Extracts the first image path from a comma-separated string of image paths.
     *
     * @param imagePaths comma-separated string of image paths
     * @return the first image path, or null if the input is null or empty
     */
    @Named("extractFirstImage")
    static String extractFirstImage(String imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return null;
        }
        return imagePaths.split(",")[0].trim();
    }

    /**
     * Splits a comma-separated string of image paths into an array.
     *
     * @param imagePaths comma-separated string of image paths
     * @return array of image paths, or empty array if input is null or empty
     */
    @Named("splitImagePaths")
    default String[] splitImagePaths(String imagePaths) {
        return imagePaths != null && !imagePaths.isEmpty() ? imagePaths.split(",") : new String[0];
    }

    /**
     * Concatenates an array of image paths into a comma-separated string.
     *
     * @param imagePaths array of image paths
     * @return comma-separated string of image paths, or empty string if input is null or empty
     */
    @Named("concatImagePaths")
    default String concatImagePaths(String[] imagePaths) {
        return imagePaths != null && imagePaths.length > 0
            ? String.join(",", imagePaths)
            : "";
    }
}

