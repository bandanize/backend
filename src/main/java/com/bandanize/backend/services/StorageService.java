package com.bandanize.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Service responsible for handling file storage operations.
 * Delegates the actual storage to an external Content Manager Service.
 */
@Service
public class StorageService {

    @Value("${cdn.url}")
    private String cdnUrl;

    private final RestTemplate restTemplate;

    public StorageService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Uploads a file to the configured CDN.
     *
     * @param file   The file to upload.
     * @param folder The target folder in the CDN (e.g., "images", "audio").
     * @return The original filename of the uploaded file.
     * @throws IOException      If file access fails.
     * @throws RuntimeException If the upload to the CDN fails.
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String contentManagerUrl = String.format("%s/upload/%s/%s", cdnUrl, folder, file.getOriginalFilename());

        HttpHeaders headers = new HttpHeaders();
        // Set the content type based on the file's type
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        try {
            // Send the request to the content manager
            restTemplate.exchange(contentManagerUrl, HttpMethod.PUT, requestEntity, String.class);
            return file.getOriginalFilename();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to CDN: " + e.getMessage(), e);
        }
    }
}
