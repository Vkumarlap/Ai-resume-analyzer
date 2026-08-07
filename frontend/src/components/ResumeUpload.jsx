import { useState } from "react";
import { analyzeText, analyzeFile } from "../api.js";

export default function ResumeUpload({ onResult, setLoading, setError }) {
  const [mode, setMode] = useState("file"); // "file" | "text"
  const [file, setFile] = useState(null);
  const [resumeText, setResumeText] = useState("");
  const [jobDescription, setJobDescription] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      let result;
      if (mode === "file") {
        if (!file) throw new Error("Please choose a .pdf or .txt file first.");
        result = await analyzeFile(file, jobDescription);
      } else {
        if (resumeText.trim().length < 50) {
          throw new Error("Paste at least a few lines of resume text.");
        }
        result = await analyzeText(resumeText, jobDescription);
      }
      onResult(result);
    } catch (err) {
      const message =
        err.response?.data?.error || err.message || "Something went wrong.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="upload-card" onSubmit={handleSubmit}>
      <div className="mode-toggle">
        <button
          type="button"
          className={mode === "file" ? "active" : ""}
          onClick={() => setMode("file")}
        >
          Upload File
        </button>
        <button
          type="button"
          className={mode === "text" ? "active" : ""}
          onClick={() => setMode("text")}
        >
          Paste Text
        </button>
      </div>

      {mode === "file" ? (
        <input
          type="file"
          accept=".pdf,.txt"
          onChange={(e) => setFile(e.target.files[0] || null)}
        />
      ) : (
        <textarea
          rows={10}
          placeholder="Paste your resume text here..."
          value={resumeText}
          onChange={(e) => setResumeText(e.target.value)}
        />
      )}

      <textarea
        rows={4}
        placeholder="(Optional) Paste a target job description for a tailored gap analysis"
        value={jobDescription}
        onChange={(e) => setJobDescription(e.target.value)}
      />

      <button type="submit" className="submit-btn">
        Analyze Resume
      </button>
    </form>
  );
}
