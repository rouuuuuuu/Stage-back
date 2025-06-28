package com.example.StageDIP.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.StageDIP.service.OCRParser;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    @Autowired
    private OCRParser ocrParser;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            File savedFile = new File("uploads/" + file.getOriginalFilename());
            file.transferTo(savedFile);

            String rawText = ocrParser.extractTextFromPdf(savedFile.getAbsolutePath());

            System.out.println("Texte OCR extrait :\n" + rawText);

            

            return ResponseEntity.ok("Texte extrait avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(" Erreur pendant l'extraction OCR.");
        }
    }
}
