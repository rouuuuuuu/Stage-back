package com.example.StageDIP.service;

import net.sourceforge.tess4j.*;

import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Produit;


import org.springframework.stereotype.Service;

@Service
public class OCRParser {

    public String extractTextFromPdf(String filePath) {
        StringBuilder fullText = new StringBuilder();
        try {

        	File file = new File("uploads/your_file.pdf");
        	PDDocument document = PDDocument.load(file); // <- STATIC method call


            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            tesseract.setLanguage("fra"); // Ou "eng"

            for (int i = 0; i < document.getNumberOfPages(); i++) {
            	PDFRenderer pdfRenderer = new PDFRenderer(document);
                File tempImg = new File("uploads/temp_page_" + i + ".png");
                BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // Render page 0

                ImageIO.write(image, "png", tempImg);

                String result = tesseract.doOCR(tempImg);
                fullText.append(result).append("\n");
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fullText.toString();
    }

    // BONUS: Extraction des objets Java
    public Facture parseFactureFromText(String rawText) {
        Facture facture = new Facture();
        Fournisseur f = new Fournisseur();
        Set<Produit> produits = new Array2DHashSet<>();

        String[] lines = rawText.split("\\r?\\n");

        for (String line : lines) {
            line = line.toLowerCase();
            if (line.contains("nom fournisseur")) {
                f.setNom(getValueAfterColon(line));
            }
            if (line.contains("adresse")) {
                f.setAdresse(getValueAfterColon(line));
            }
            if (line.contains("email")) {
                f.setEmail(getValueAfterColon(line));
            }
            if (line.contains("notation")) {
                try {
                    f.setNotation(Double.parseDouble(getValueAfterColon(line)));
                } catch (Exception e) { /* ignore */ }
            }

            if (line.contains("produit") || line.contains("prix")) {
                Produit p = new Produit();
                if (line.contains("produit")) p.setNom(getValueAfterKeyword(line, "produit"));
                if (line.contains("catégorie")) p.setCategorie(getValueAfterKeyword(line, "catégorie"));
                if (line.contains("prix")) {
                    try {
                        p.setPrixUnitaire(Double.parseDouble(getValueAfterKeyword(line, "prix")));
                    } catch (Exception e) { /* ignore */ }
                }
                p.setFournisseur(f);
                produits.add(p);
            }

            if (line.contains("date")) {
                try {
                    facture.setDate(LocalDate.parse(getValueAfterColon(line).trim())); // Assume format is correct
                } catch (Exception e) { /* handle parsing if needed */ }
            }

            if (line.contains("montant")) {
                try {
                    facture.setMontantTotal(Double.parseDouble(getValueAfterColon(line)));
                } catch (Exception e) { /* ignore */ }
            }
        }

        facture.setFournisseur(f);
        facture.setProduits(produits);
        return facture;
    }

    private String getValueAfterColon(String line) {
        return line.contains(":") ? line.split(":", 2)[1].trim() : "";
    }

    private String getValueAfterKeyword(String line, String keyword) {
        String[] parts = line.split(",");
        for (String part : parts) {
            if (part.contains(keyword)) {
                return part.split(":", 2)[1].trim();
            }
        }
        return "";
    }
}
