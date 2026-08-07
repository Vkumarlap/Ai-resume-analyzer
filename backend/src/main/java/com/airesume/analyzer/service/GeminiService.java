package com.airesume.analyzer.service;

import com.airesume.analyzer.dto.ResumeAnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public GeminiService(WebClient geminiWebClient,
                         ObjectMapper objectMapper) {
        this.webClient = geminiWebClient;
        this.objectMapper = objectMapper;
    }

    public ResumeAnalysisResponse analyzeResume(String resumeText,
                                                String jobDescription) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API Key is missing.");
        }

        String prompt = buildPrompt(resumeText, jobDescription);

        String rawResponse = callGemini(prompt);

        return parseModelResponse(rawResponse);
    }

    private String buildPrompt(String resumeText, String jobDescription) {

        StringBuilder sb = new StringBuilder();

        sb.append("""
                You are an expert ATS and Technical Recruiter.

                Analyze the resume and return ONLY valid JSON.

                {
                  "atsScore": 0,
                  "strengths": [],
                  "weaknesses": [],
                  "missingKeywords": [],
                  "suggestions": [],
                  "interviewQuestions": [],
                  "summary":""
                }

                """);

        if (jobDescription != null && !jobDescription.isBlank()) {

            sb.append("""
                    
                    JOB DESCRIPTION
                    
                    """);

            sb.append(jobDescription);

            sb.append("""

                    END JOB DESCRIPTION

                    """);
        }

        sb.append("""

                RESUME

                """);

        sb.append(resumeText);

        return sb.toString();
    }

    private String callGemini(String prompt) {

        Map<String, Object> body = Map.of(
                "contents",
                new Object[]{
                        Map.of(
                                "parts",
                                new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                },
                "generationConfig",
                Map.of(
                        "temperature", 0.4,
                        "responseMimeType", "application/json"
                )
        );

        JsonNode response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("generativelanguage.googleapis.com")
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .map(error -> {
                                            log.error("Gemini Error : {}", error);
                                            return new RuntimeException(error);
                                        })
                )
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(60))
                .block();

        log.info("Gemini Response : {}", response);

        if (response == null) {
            throw new RuntimeException("Empty response from Gemini");
        }

        JsonNode textNode = response
                .path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (textNode.isMissingNode()) {
            throw new RuntimeException("Unable to read Gemini response");
        }

        return textNode.asText();
    }

    private ResumeAnalysisResponse parseModelResponse(String rawText) {

        try {

            String cleaned = rawText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    cleaned,
                    ResumeAnalysisResponse.class
            );

        } catch (Exception e) {

            log.error(rawText);

            throw new RuntimeException("Invalid JSON returned by Gemini", e);
        }
    }
}