package com.bandanize.backend.controllers;

import com.bandanize.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for handling file uploads.
 * Exposes endpoints for uploading images, audio, video, and generic files.
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final StorageService storageService;

    @Autowired
    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Uploads an image file.
     *
     * @param file The image file to upload.
     * @return ResponseEntity with the upload status.
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFileInternal(file, "images");
    }

    /**
     * Uploads an audio file.
     *
     * @param file The audio file to upload.
     * @return ResponseEntity with the upload status.
     */
    @PostMapping("/audio")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        return uploadFileInternal(file, "audio");
    }

    /**
     * Uploads a video file.
     *
     * @param file The video file to upload.
     * @return ResponseEntity with the upload status.
     */
    @PostMapping("/video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        return uploadFileInternal(file, "videos");
    }

    /**
     * Uploads a generic file.
     *
     * @param file The file to upload.
     * @return ResponseEntity with the upload status.
     */
    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return uploadFileInternal(file, "files");
    }

    /**
     * Internal helper method to delegate the upload to the service.
     */
    private ResponseEntity<String> uploadFileInternal(MultipartFile file, String folder) {
        try {
            String filename = storageService.uploadFile(file, folder);
            return ResponseEntity.ok("File uploaded successfully: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }
}