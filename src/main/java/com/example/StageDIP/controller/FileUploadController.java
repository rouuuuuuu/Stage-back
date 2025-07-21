package com.example.StageDIP.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.StageDIP.service.FileService;

import jakarta.validation.Valid;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final FileService fileService;

    private static final long MAX_FILE_SIZE = 40 * 1024 * 1024; 
    private static final String[] ALLOWED_TYPES = {
            "application/pdf",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };

    // Inject FileService using constructor
    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@Valid @RequestParam("file") MultipartFile file) {
        System.out.println("Request received: " + file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fichier vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fichier trop gros, limite à 40MB.");
        }

        String contentType = file.getContentType();
        boolean validType = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equalsIgnoreCase(contentType)) {
                validType = true;
                break;
            }
        }

        if (!validType) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Format non supporté. PDF ou Excel only.");
        }

        try {
            String savedPath = fileService.saveFile(file);
            System.out.println("File saved at: " + savedPath);
            return ResponseEntity.ok("Upload réussi. Sauvé dans: " + savedPath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement du fichier.");
        }
    }
}
