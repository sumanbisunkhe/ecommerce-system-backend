package com.example.ecommerce.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ecommerce.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.directory:uploads}")
    private String uploadDir;
    
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        String targetDir = directory != null ? directory : uploadDir;

        // Create directory if it doesn't exist
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path targetPath = Paths.get(targetDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath);

        log.info("File uploaded successfully: {}", targetPath);

        return targetPath.toString();
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            Path filePath = Paths.get(fileUrl);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return false;
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        // List of valid image content types
        String[] validImageTypes = {
                "image/jpeg", "image/png", "image/gif", "image/webp"
        };

        return Arrays.stream(validImageTypes).anyMatch(contentType::equals);
    }

    @Override
    public Map<String, String> uploadImage(MultipartFile file, Map<String, Object> options) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        String targetDir = options != null && options.containsKey("directory") ?
                options.get("directory").toString() : uploadDir + "/images";

        // Create directory if it doesn't exist
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path targetPath = Paths.get(targetDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath);

        log.info("Image uploaded successfully: {}", targetPath);

        // Return image details
        Map<String, String> result = new HashMap<>();
        result.put("url", targetPath.toString());
        result.put("publicId", filename);
        result.put("originalFilename", originalFilename);

        return result;
    }

    @Override
    public Map<String, String> uploadImage(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) throw new IOException("Failed to store empty file");

        File uploadedFile = convertMultiPartToFile(file);

        try {
            String uniqueId = UUID.randomUUID().toString();

            Map params = ObjectUtils.asMap(
                    "folder", directory,
                    "public_id", uniqueId,
                    "overwrite", true,
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(uploadedFile, params);
            uploadedFile.delete();

            Map<String, String> result = new HashMap<>();
            result.put("secure_url", (String) uploadResult.get("secure_url"));
            result.put("url", (String) uploadResult.get("url"));
            result.put("publicId", (String) uploadResult.get("public_id"));
            result.put("originalFilename", file.getOriginalFilename());

            return result;
        } catch (Exception e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }
    
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    @Override
    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            return;
        }

        try {
            // Try to find the image in the images directory
            Path imagePath = Paths.get(uploadDir, "images", publicId);
            if (Files.exists(imagePath)) {
                Files.deleteIfExists(imagePath);
                return;
            }

            // If not found, try to find it directly in the upload directory
            imagePath = Paths.get(uploadDir, publicId);
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            log.error("Error deleting image with publicId: {}", publicId, e);
        }
    }
}