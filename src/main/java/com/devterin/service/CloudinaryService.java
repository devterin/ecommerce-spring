package com.devterin.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.devterin.ultil.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("Upload failed: File is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            log.error("Upload failed: Invalid MIME type {}", contentType);
            throw new IllegalArgumentException("Only jpg, png formats are allowed");
        }
    }

}
