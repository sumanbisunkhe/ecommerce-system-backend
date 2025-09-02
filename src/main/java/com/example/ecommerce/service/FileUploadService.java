package com.example.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String directory) throws IOException;

    boolean deleteFile(String fileUrl);

    boolean isValidImage(MultipartFile file);

    Map<String, String> uploadImage(MultipartFile file, Map<String, Object> options) throws IOException;

    Map<String, String> uploadImage(MultipartFile file, String directory) throws IOException;

    void deleteImage(String publicId);
}
