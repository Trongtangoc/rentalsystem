
package com.codegym.projectmodule5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@Slf4j
public class ImageDebugController {

    @GetMapping("/images")
    public Map<String, Object> checkImages() {
        Map<String, Object> result = new HashMap<>();

        String currentDir = System.getProperty("user.dir");
        result.put("currentDirectory", currentDir);

        File uploadsDir = new File(currentDir + "/uploads");
        result.put("uploadsExists", uploadsDir.exists());
        result.put("uploadsPath", uploadsDir.getAbsolutePath());

        File imagesDir = new File(currentDir + "/uploads/images");
        result.put("imagesExists", imagesDir.exists());
        result.put("imagesPath", imagesDir.getAbsolutePath());

        if (imagesDir.exists()) {
            File[] files = imagesDir.listFiles();
            if (files != null) {
                result.put("imageCount", files.length);

                List<Map<String, Object>> fileDetails = new ArrayList<>();
                for (File file : files) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", file.getName());
                    fileInfo.put("size", file.length());
                    fileInfo.put("readable", file.canRead());
                    fileInfo.put("url", "/uploads/images/" + file.getName());
                    fileDetails.add(fileInfo);
                }
                result.put("files", fileDetails);

                // Kiểm tra các file mà DataInitializer đang tìm
                String[] expectedFiles = {"house1.jpg", "house2.jpg", "1.png", "2.webp", "p1.avif", "pv1.jpg", "pv2.jpg", "pv3.jpg"};
                Map<String, Boolean> expectedFilesStatus = new HashMap<>();
                for (String expectedFile : expectedFiles) {
                    File file = new File(imagesDir, expectedFile);
                    expectedFilesStatus.put(expectedFile, file.exists());
                }
                result.put("expectedFiles", expectedFilesStatus);

            } else {
                result.put("imageCount", 0);
                result.put("files", new ArrayList<>());
            }
        }

        return result;
    }

    @GetMapping("/test-image-access")
    public Map<String, Object> testImageAccess() {
        Map<String, Object> result = new HashMap<>();

        String currentDir = System.getProperty("user.dir");
        File imagesDir = new File(currentDir + "/uploads/images");

        if (imagesDir.exists()) {
            File[] files = imagesDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") ||
                            name.toLowerCase().endsWith(".png") ||
                            name.toLowerCase().endsWith(".webp") ||
                            name.toLowerCase().endsWith(".avif"));

            if (files != null && files.length > 0) {
                List<String> testUrls = new ArrayList<>();
                for (File file : files) {
                    String url = "http://localhost:8080/uploads/images/" + file.getName();
                    testUrls.add(url);
                }
                result.put("testUrls", testUrls);
                result.put("message", "Try accessing these URLs in browser");
            } else {
                result.put("message", "No image files found");
            }
        } else {
            result.put("message", "Images directory not found");
        }

        return result;
    }

    @GetMapping("/create-sample-data")
    public Map<String, Object> createSampleImageData() {
        Map<String, Object> result = new HashMap<>();

        try {
            String currentDir = System.getProperty("user.dir");
            File imagesDir = new File(currentDir + "/uploads/images");

            if (!imagesDir.exists()) {
                boolean created = imagesDir.mkdirs();
                result.put("directoryCreated", created);
            }

            // Tạo file text để test (không phải hình ảnh thực)
            String[] sampleFiles = {"house1.jpg", "house2.jpg", "house3.jpg"};
            List<String> createdFiles = new ArrayList<>();

            for (String fileName : sampleFiles) {
                File testFile = new File(imagesDir, fileName);
                if (!testFile.exists()) {
                    try {
                        boolean created = testFile.createNewFile();
                        if (created) {
                            createdFiles.add(fileName);
                        }
                    } catch (Exception e) {
                        log.error("Could not create file: " + fileName, e);
                    }
                }
            }

            result.put("createdFiles", createdFiles);
            result.put("message", "Sample files created (empty files for testing)");

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }
}