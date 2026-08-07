import { useState } from "react";
import ResumeUpload from "./components/ResumeUpload.jsx";
import AnalysisResult from "./components/AnalysisResult.jsx";

export default function App() {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  return (
    <div className="app">
      <header>
        <h1>AI Resume Analyzer</h1>
        <p>Spring Boot + Gemini API + React</p>
      </header>

      <ResumeUpload
        onResult={(r) => setResult(r)}
        setLoading={setLoading}
        setError={setError}
      />

      {loading && <p className="status">Analyzing your resume...</p>}
      {error && <p className="status error">{error}</p>}

      <AnalysisResult result={result} />
    </div>
  );
}
