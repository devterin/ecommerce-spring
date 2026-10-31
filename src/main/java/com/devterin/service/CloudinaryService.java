package com.devterin.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.devterin.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        validateFile(file);
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String fullFolder = AppConstants.FOLDER + folder;
        try {
            var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", fullFolder,
                    "public_id", fileName,
                    "resource_type", "image"));

            return result.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Upload failed for file {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()
                || imageUrl.equals(AppConstants.URL_DEFAULT_AVATAR)
                || !imageUrl.contains("/image/upload/")) {
            return;
        }

        try {
            String publicId = new URI(imageUrl).getPath();
            publicId = publicId.substring(publicId.indexOf("/upload/") + "/upload/".length());
            publicId = publicId.replaceFirst("^v\\d+/", "");
            int extensionIndex = publicId.lastIndexOf('.');
            if (extensionIndex > -1) {
                publicId = publicId.substring(0, extensionIndex);
            }
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (IOException | URISyntaxException e) {
            log.warn("Unable to delete avatar {}", imageUrl, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("Upload failed: File is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            log.error("Upload failed: Invalid MIME type {}", contentType);
            throw new IllegalArgumentException("Only jpg, png formats are allowed");
        }
        if (file.getSize() > 3 * 1024 * 1024) {
            log.error("Upload failed: File size exceeds 3MB");
            throw new IllegalArgumentException("File size exceeds limit of 3MB");
        }
    }

}
