package at.ac.tuwien.sepr.groupphase.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    /**
     * Save an image to your local storage.
     *
     * @param image The image that gets saved.
     * @return Unique file name of the saved image.
     */
    String saveImageToDirectory(MultipartFile image) throws IOException;


    /**
     * Delete an image from your local storage.
     *
     * @param imageDirectory The directory that specifies, where the image is saved.
     * @param imageName The  name of the image to delete.
     */
    void deleteImageFromLocalStorage(String imageDirectory, String imageName) throws IOException;
}
