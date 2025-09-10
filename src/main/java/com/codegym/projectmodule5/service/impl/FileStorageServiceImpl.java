//package com.codegym.projectmodule5.service.impl;
//
//import com.codegym.projectmodule5.exception.CustomException;
//import com.codegym.projectmodule5.service.FileStorageService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class FileStorageServiceImpl implements FileStorageService {
//
//    @Value("${file.upload-dir:uploads}")
//    private String uploadDir;
//
//    @Value("${file.max-size:10485760}") // 10MB
//    private long maxFileSize;
//
//    private final List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx");
//
//    @Override
//    public String uploadFile(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new CustomException("File is empty");
//        }
//
//        if (file.getSize() > maxFileSize) {
//            throw new CustomException("File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB");
//        }
//
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null) {
//            throw new CustomException("Invalid file name");
//        }
//
//        String fileExtension = getFileExtension(originalFilename);
//        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
//            throw new CustomException("File type not allowed. Allowed types: " + String.join(", ", allowedExtensions));
//        }
//
//        try {
//            // Create upload directory if it doesn't exist
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            // Generate unique filename
//            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
//            Path filePath = uploadPath.resolve(uniqueFilename);
//
//            // Copy file to the target location
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            log.info("File uploaded successfully: {}", uniqueFilename);
//            return "/uploads/" + uniqueFilename; // Return URL path
//
//        } catch (IOException e) {
//            log.error("Error uploading file: {}", e.getMessage());
//            throw new CustomException("Failed to upload file: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public List<String> uploadFiles(List<MultipartFile> files) {
//        return files.stream()
//                .map(this::uploadFile)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void deleteFile(String fileName) {
//        try {
//            // Extract filename from URL path
//            String actualFileName = fileName.replace("/uploads/", "");
//            Path filePath = Paths.get(uploadDir, actualFileName);
//
//            if (Files.exists(filePath)) {
//                Files.delete(filePath);
//                log.info("File deleted successfully: {}", actualFileName);
//            } else {
//                log.warn("File not found for deletion: {}", actualFileName);
//            }
//        } catch (IOException e) {
//            log.error("Error deleting file: {}", e.getMessage());
//            throw new CustomException("Failed to delete file: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public boolean fileExists(String fileName) {
//        try {
//            String actualFileName = fileName.replace("/uploads/", "");
//            Path filePath = Paths.get(uploadDir, actualFileName);
//            return Files.exists(filePath);
//        } catch (Exception e) {
//            log.error("Error checking file existence: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    private String getFileExtension(String filename) {
//        int lastDotIndex = filename.lastIndexOf('.');
//        if (lastDotIndex == -1) {
//            return "";
//        }
//        return filename.substring(lastDotIndex + 1);
//    }
//}

package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
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

    private final List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "webp");

    private Path uploadPath;
    private Path imagesPath;

    @PostConstruct
    public void init() {
        try {
            // Tạo đường dẫn đến thư mục uploads
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // Tạo đường dẫn đến thư mục uploads/images
            imagesPath = uploadPath.resolve("images").toAbsolutePath().normalize();

            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            if (!Files.exists(imagesPath)) {
                Files.createDirectories(imagesPath);
                log.info("Created images directory: {}", imagesPath);
            }

            log.info("File storage initialized:");
            log.info("Upload directory: {}", uploadPath);
            log.info("Images directory: {}", imagesPath);

        } catch (IOException e) {
            log.error("Failed to initialize file storage directories", e);
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        try {
            // Tạo tên file unique
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // Lưu vào thư mục uploads/images
            Path targetPath = imagesPath.resolve(uniqueFilename);

            // Copy file
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL path để truy cập từ web
            String fileUrl = "/uploads/images/" + uniqueFilename;

            log.info("File uploaded successfully: {} -> {}", originalFilename, fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new CustomException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new CustomException("No files provided");
        }

        if (files.size() > 10) {
            throw new CustomException("Maximum 10 files allowed per upload");
        }

        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new CustomException("File name cannot be empty");
            }

            // Xử lý cả trường hợp URL đầy đủ và tên file
            String actualFileName;
            if (fileName.startsWith("/uploads/images/")) {
                actualFileName = fileName.replace("/uploads/images/", "");
            } else if (fileName.startsWith("/uploads/")) {
                actualFileName = fileName.replace("/uploads/", "");
            } else {
                actualFileName = fileName;
            }

            Path filePath = imagesPath.resolve(actualFileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully: {}", actualFileName);
            } else {
                log.warn("File not found for deletion: {}", filePath);
                throw new CustomException("File not found: " + actualFileName);
            }

        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw new CustomException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return false;
            }

            String actualFileName;
            if (fileName.startsWith("/uploads/images/")) {
                actualFileName = fileName.replace("/uploads/images/", "");
            } else if (fileName.startsWith("/uploads/")) {
                actualFileName = fileName.replace("/uploads/", "");
            } else {
                actualFileName = fileName;
            }

            Path filePath = imagesPath.resolve(actualFileName);
            return Files.exists(filePath);

        } catch (Exception e) {
            log.error("Error checking file existence: {}", e.getMessage(), e);
            return false;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new CustomException("File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new CustomException("Invalid file name");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (fileExtension.isEmpty()) {
            throw new CustomException("File must have an extension");
        }

        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new CustomException("File type not allowed. Allowed types: " + String.join(", ", allowedExtensions));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1);
    }
}