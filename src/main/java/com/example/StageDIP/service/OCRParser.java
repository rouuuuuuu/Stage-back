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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Date;


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
	        File file = new File(filePath);  // Use the passed file path, duh
	        PDDocument document = PDDocument.load(file);
	        Tesseract tesseract = new Tesseract();
	        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // Make sure this path is correct on your machine or server
	        tesseract.setLanguage("fra"); // Or "eng"

	        PDFRenderer pdfRenderer = new PDFRenderer(document);

	        for (int i = 0; i < document.getNumberOfPages(); i++) {
	            File tempImg = new File("uploads/temp_page_" + i + ".png");
	            BufferedImage image = pdfRenderer.renderImageWithDPI(i, 300); // Render page i, not 0

	            ImageIO.write(image, "png", tempImg);

	            String result = tesseract.doOCR(tempImg);
	            fullText.append(result).append("\n");

	            tempImg.delete(); // Clean up temp image to avoid disk clutter
	        }
	        document.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return fullText.toString();
	}

	public Facture parseFactureFromText(String rawText) {
	    Facture facture = new Facture();
	    Fournisseur f = new Fournisseur();
	    Set<Produit> produits = new HashSet<>();

	    // Prepare date parser (adjust pattern if needed)
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
	            } catch (Exception e) {
	                // Ignore bad notation
	            }
	        }

	        if (line.contains("produit") || line.contains("prix")) {
	            Produit p = new Produit();

	            if (line.contains("produit")) {
	                p.setNom(getValueAfterKeyword(line, "produit"));
	            }

	            if (line.contains("catégorie")) {
	                p.setCategorie(getValueAfterKeyword(line, "catégorie"));
	            }

	            if (line.contains("prix")) {
	                try {
	                    p.setPrixUnitaire(Double.parseDouble(getValueAfterKeyword(line, "prix")));
	                } catch (Exception e) {
	                    // Ignore bad price
	                }
	            }

	            p.setFournisseur(f); // Link produit to its fournisseur
	            produits.add(p);
	        }

	        if (line.contains("date")) {
	            try {
	                String dateStr = getValueAfterColon(line).trim();
	                Date parsedDate = sdf.parse(dateStr); // Ex: 23/06/2024
	                facture.setDate(parsedDate);
	            } catch (Exception e) {
	                // Log or handle bad date
	            }
	        }

	        if (line.contains("montant")) {
	            try {
	                facture.setMontantTotal(Double.parseDouble(getValueAfterColon(line)));
	            } catch (Exception e) {
	                // Ignore bad amount
	            }
	        }
	    }

	    facture.setFournisseur(f);
	    facture.setProduits(produits);
	    return facture;
	}

	// 🔸 Helper methods
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
	}}