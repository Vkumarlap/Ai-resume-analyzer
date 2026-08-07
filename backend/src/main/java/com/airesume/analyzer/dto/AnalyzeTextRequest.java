package com.airesume.analyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnalyzeTextRequest(

        @NotBlank(message = "Resume text must not be empty")
        @Size(min = 50, message = "Resume text looks too short to analyze")
        String resumeText,

        
        String jobDescription
) {
}
