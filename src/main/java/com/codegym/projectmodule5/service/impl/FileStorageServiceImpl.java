package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Copy file to the target location
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded successfully: {}", uniqueFilename);
            return "/uploads/" + uniqueFilename; // Return URL path

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
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
            String actualFileName = fileName.replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir, actualFileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", actualFileName);
            } else {
                log.warn("File not found for deletion: {}", actualFileName);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new CustomException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            String actualFileName = fileName.replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir, actualFileName);
            return Files.exists(filePath);
        } catch (Exception e) {
            log.error("Error checking file existence: {}", e.getMessage());
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