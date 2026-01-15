package com.bandanize.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements FileStorageService {

    @Value("${storage.location}")
    private String storageLocation;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        if (storageLocation == null || storageLocation.trim().isEmpty()) {
            storageLocation = "uploads";
        }
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocation.resolve("images"));
            Files.createDirectories(rootLocation.resolve("audio"));
            Files.createDirectories(rootLocation.resolve("videos"));
            Files.createDirectories(rootLocation.resolve("files"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        // Generate unique filename to prevent overwrites
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }

            Path folderPath = this.rootLocation.resolve(folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, folderPath.resolve(uniqueFilename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }

        // Return the full URL (or relative path if frontend builds it)
        // Since we are replacing the CDN logic which returned a URL potentially,
        // we should check what the frontend expects.
        // Original StorageService returned: file.getOriginalFilename() (and
        // UploadController built response)
        // Checks UploadController... it returns "File uploaded successfully: " +
        // filename.

        // Wait, UploadController called storageService.uploadFile -> returned filename.
        // But logic in StorageService built "contentManagerUrl" but ultimately returned
        // "file.getOriginalFilename()".
        // The frontend likely constructs the URL using the cdnUrl + folder + filename.

        // If we are serving locally, we want to return the filename that was ACTUALLY
        // saved (UUID included).
        return uniqueFilename;
    }

    @Override
    public Resource load(String filename, String folder) {
        try {
            Path file = rootLocation.resolve(folder).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void delete(String filename, String folder) {
        try {
            Path file = rootLocation.resolve(folder).resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }
}
