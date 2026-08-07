function Section({ title, items }) {
  if (!items || items.length === 0) return null;
  return (
    <div className="result-section">
      <h3>{title}</h3>
      <ul>
        {items.map((item, i) => (
          <li key={i}>{item}</li>
        ))}
      </ul>
    </div>
  );
}

export default function AnalysisResult({ result }) {
  if (!result) return null;

  const scoreColor =
    result.atsScore >= 75 ? "#1a9c4a" : result.atsScore >= 50 ? "#c98a12" : "#c9331b";

  return (
    <div className="result-card">
      <div className="score-row">
        <div className="score-circle" style={{ borderColor: scoreColor, color: scoreColor }}>
          {result.atsScore ?? "-"}
        </div>
        <div>
          <h2>ATS Score</h2>
          <p>{result.summary}</p>
        </div>
      </div>

      <Section title="Strengths" items={result.strengths} />
      <Section title="Weaknesses" items={result.weaknesses} />
      <Section title="Missing Keywords" items={result.missingKeywords} />
      <Section title="Suggestions" items={result.suggestions} />
      <Section title="Likely Interview Questions" items={result.interviewQuestions} />
    </div>
  );
}
