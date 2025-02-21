package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${app.storage.image-directory}")
    private String imageDirectory;
    @Value("${app.image-base-url}")
    private String imageBaseUrl;

    public ImageServiceImpl() {
        super();
    }

    @Override
    public String saveImageToDirectory(MultipartFile image) throws IOException {
        LOGGER.debug("Save image with original name {}", image.getOriginalFilename());

        try {
            // Sanitize and create a unique filename
            String originalFilename = image.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("Image file must have a name");
            }

            String sanitizedFilename = originalFilename.replaceAll("\\s+", "_") // Replace spaces with underscores
                .replaceAll("[^a-zA-Z0-9._-]", ""); // Remove unsafe characters
            String uniqueFilename = UUID.randomUUID() + "_" + sanitizedFilename;

            // Create file path
            Path imagePath = Paths.get(imageDirectory, uniqueFilename);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, image.getBytes());

            LOGGER.info("Image saved to {}", imagePath);
            return imageBaseUrl + uniqueFilename;
        } catch (IOException e) {
            LOGGER.error("Failed to save image", e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    /*
    @Override
    public byte[] getImageFromLocalStorage(String imageDirectory, String imageName) throws IOException {
        LOGGER.debug("Get image from local storage with name {}", imageName);

        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        } else {
            throw new NotFoundException();
        }
    }
     */

    @Override
    public void deleteImageFromLocalStorage(String imageDirectory, String imageName) throws IOException {
        LOGGER.debug("Delete image from local storage with name {}", imageName);

        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        } else {
            throw new NotFoundException();
        }
    }
}
