//package com.codegym.projectmodule5.controller;
//
//import com.codegym.projectmodule5.dto.response.ApiResponse;
//import com.codegym.projectmodule5.service.FileStorageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/files")
//public class FileUploadController {
//
//    private final FileStorageService fileStorageService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            String fileUrl = fileStorageService.uploadFile(file);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "File uploaded successfully");
//            response.put("fileUrl", fileUrl);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/upload-multiple")
//    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
//        try {
//            List<String> fileUrls = fileStorageService.uploadFiles(files);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Files uploaded successfully");
//            response.put("fileUrls", fileUrls);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @DeleteMapping
//    public ResponseEntity<ApiResponse> deleteFile(@RequestParam("fileName") String fileName) {
//        try {
//            fileStorageService.deleteFile(fileName);
//            return ResponseEntity.ok(new ApiResponse(true, "File deleted successfully"));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
//        }
//    }
//}

package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Slf4j
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received file upload request: {}", file.getOriginalFilename());

            String fileUrl = fileStorageService.uploadFile(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", fileUrl);
            response.put("fileName", file.getOriginalFilename());

            log.info("File uploaded successfully: {}", fileUrl);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("File upload failed: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            log.info("Received multiple file upload request: {} files", files.size());

            if (files.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No files selected");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file count
            if (files.size() > 10) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Maximum 10 files allowed");
                return ResponseEntity.badRequest().body(response);
            }

            List<String> fileUrls = fileStorageService.uploadFiles(files);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", files.size() + " files uploaded successfully");
            response.put("fileUrls", fileUrls);
            response.put("count", fileUrls.size());

            log.info("Multiple files uploaded successfully: {} files", fileUrls.size());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Multiple file upload failed: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            log.info("Received file deletion request: {}", fileName);

            if (fileName == null || fileName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "File name is required"));
            }

            fileStorageService.deleteFile(fileName);

            log.info("File deleted successfully: {}", fileName);
            return ResponseEntity.ok(new ApiResponse(true, "File deleted successfully"));

        } catch (RuntimeException e) {
            log.error("File deletion failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Delete failed: " + e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> checkFileExists(@RequestParam("fileName") String fileName) {
        try {
            boolean exists = fileStorageService.fileExists(fileName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            response.put("fileName", fileName);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("File existence check failed: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("exists", false);

            return ResponseEntity.badRequest().body(response);
        }
    }
}