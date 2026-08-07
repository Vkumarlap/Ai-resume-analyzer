package com.airesume.analyzer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeParserService {

    public String extractText(MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();

        try {
            if (filename.endsWith(".pdf")) {
                return extractFromPdf(file);
            } else if (filename.endsWith(".txt")) {
                return new String(file.getBytes());
            } else {
                throw new IllegalArgumentException(
                        "Unsupported file type. Please upload a .pdf or .txt resume.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read uploaded file: " + e.getMessage(), e);
        }
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("Encrypted PDFs are not supported.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException(
                        "Could not extract any text from this PDF (it may be a scanned image).");
            }
            return text;
        }
    }
}
