# AI Resume Analyzer

Spring Boot backend + React (Vite) frontend that analyzes a resume using the
Gemini API and returns an ATS score, strengths/weaknesses, missing keywords,
suggestions, and likely interview questions.

```
ai-resume-analyzer/
├── backend/   Spring Boot 3 (Java 17) — calls the Gemini API
└── frontend/  React + Vite — upload/paste UI, renders results
```

## 1. Get a Gemini API key

1. Go to https://aistudio.google.com/app/apikey
2. Create an API key (free tier is enough to run this).
3. Don't paste it into any file in this project — it's read from an
   environment variable so it never gets committed to git.

## 2. Run the backend

```bash
cd backend
export GEMINI_API_KEY=your-real-key-here
./mvnw spring-boot:run          # or: mvn spring-boot:run
```

The API starts on **http://localhost:8080**. Sanity check:

```bash
curl http://localhost:8080/api/resume/health
```

### Endpoints

| Method | Path                        | Body                                                  |
|--------|-----------------------------|--------------------------------------------------------|
| POST   | `/api/resume/analyze-text`  | `{ "resumeText": "...", "jobDescription": "..." }`     |
| POST   | `/api/resume/analyze-file`  | multipart: `file` (.pdf or .txt), `jobDescription`     |

Both return:

```json
{
  "atsScore": 78,
  "strengths": ["..."],
  "weaknesses": ["..."],
  "missingKeywords": ["..."],
  "suggestions": ["..."],
  "interviewQuestions": ["..."],
  "summary": "..."
}
```

## 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. It's already configured to call the backend
at `http://localhost:8080`.

## Notes / next steps

- **Rate limits & cost**: the free Gemini tier has request-per-minute limits;
  if you get a 429, wait a moment and retry.
- **Security**: this is a learning/demo setup — the API key lives only on the
  backend (never sent to the browser), which is correct. Before deploying
  anywhere public, add auth on the endpoints and rate-limiting so a stranger
  can't burn through your Gemini quota.
- **Scanned PDFs**: text extraction uses Apache PDFBox, which needs a PDF with
  a real text layer — scanned/image-only resumes won't extract cleanly
  without adding OCR (e.g., Tesseract).
- **RAG / vector DB**: not included here. If you want it, the natural next
  step is embedding a library of job descriptions (via the Gemini embeddings
  endpoint) into a vector store (e.g., pgvector or Pinecone) and retrieving
  the closest ones to enrich the prompt.
