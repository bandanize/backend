package com.bandanize.backend.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "images");
    }

    @PostMapping("/audio")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "audio");
    }

    @PostMapping("/video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "videos");
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "files");
    }

    private ResponseEntity<String> uploadFile(MultipartFile file, String folder) {
        try {
            // Build the URL for the content manager with the corresponding folder
            String contentManagerUrl = "http://localhost:8181/upload/" + folder + "/" + file.getOriginalFilename();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
    
            // Send the request to the content manager
            restTemplate.exchange(contentManagerUrl, HttpMethod.PUT, requestEntity, String.class);
    
            // Return a success message with the file name
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }
}