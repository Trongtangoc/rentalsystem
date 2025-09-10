package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.max-size:10485760}") // 10MB
    private long maxFileSize;

    private final List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx");

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new CustomException("File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CustomException("Invalid file name");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new CustomException("File type not allowed. Allowed types: " + String.join(", ", allowedExtensions));
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Create images subdirectory if it doesn't exist
            Path imagesPath = uploadPath.resolve("images");
            if (!Files.exists(imagesPath)) {
                Files.createDirectories(imagesPath);
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = imagesPath.resolve(uniqueFilename);

            // Copy file to the target location
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/images/" + uniqueFilename; // Return URL path

        } catch (IOException e) {
            throw new CustomException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            // Extract filename from URL path
            String actualFileName = fileName.replace("/uploads/images/", "").replace("/uploads/", "");
            Path imagesPath = Paths.get(uploadDir, "images");
            Path filePath = imagesPath.resolve(actualFileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new CustomException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            String actualFileName = fileName.replace("/uploads/images/", "").replace("/uploads/", "");
            Path imagesPath = Paths.get(uploadDir, "images");
            Path filePath = imagesPath.resolve(actualFileName);
            return Files.exists(filePath);
        } catch (Exception e) {
            return false;
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}