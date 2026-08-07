import axios from "axios";

const BASE_URL = "http://localhost:8080/api/resume";

export async function analyzeText(resumeText, jobDescription) {
  const { data } = await axios.post(`${BASE_URL}/analyze-text`, {
    resumeText,
    jobDescription,
  });
  return data;
}

export async function analyzeFile(file, jobDescription) {
  const formData = new FormData();
  formData.append("file", file);
  if (jobDescription) formData.append("jobDescription", jobDescription);

  const { data } = await axios.post(`${BASE_URL}/analyze-file`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
}
