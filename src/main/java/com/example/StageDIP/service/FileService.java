package com.example.StageDIP.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

	private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/uploads";

    public String saveFile(MultipartFile file) throws IOException {
        // Make sure upload dir exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Build file path
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Invalid file name.");
        }
        // Avoid overwriting by adding timestamp
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        // Save the file to disk
        file.transferTo(filePath.toFile());

        // Return the absolute path of the saved file
        return filePath.toString();
    }
}
