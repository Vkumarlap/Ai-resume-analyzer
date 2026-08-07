package com.airesume.analyzer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResumeAnalysisResponse(
        Integer atsScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingKeywords,
        List<String> suggestions,
        List<String> interviewQuestions,
        String summary
) {
}
