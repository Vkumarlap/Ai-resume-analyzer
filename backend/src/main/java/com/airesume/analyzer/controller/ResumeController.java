package com.airesume.analyzer.controller;

import com.airesume.analyzer.dto.AnalyzeTextRequest;
import com.airesume.analyzer.dto.ResumeAnalysisResponse;
import com.airesume.analyzer.service.GeminiService;
import com.airesume.analyzer.service.ResumeParserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final GeminiService geminiService;
    private final ResumeParserService resumeParserService;

    /** Analyze resume text pasted directly by the user (optionally against a job description). */
    @PostMapping("/analyze-text")
    public ResponseEntity<ResumeAnalysisResponse> analyzeText(@Valid @RequestBody AnalyzeTextRequest request) {
        ResumeAnalysisResponse result = geminiService.analyzeResume(request.resumeText(), request.jobDescription());
        return ResponseEntity.ok(result);
    }

    /** Analyze an uploaded resume file (.pdf or .txt). */
    @PostMapping(value = "/analyze-file", consumes = "multipart/form-data")
    public ResponseEntity<ResumeAnalysisResponse> analyzeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {

        String resumeText = resumeParserService.extractText(file);
        ResumeAnalysisResponse result = geminiService.analyzeResume(resumeText, jobDescription);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Resume Analyzer backend is running");
    }
}
