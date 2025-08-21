````markdown
# 🔍 Semantic Search RAG in Java

A **Retrieval Augmented Generation (RAG)** system implemented in **Java**,  
combining **Semantic Search** with **LLMs** to answer questions using a knowledge base stored in **Milvus (Zilliz Cloud)**.

---

## ⚡ Overview
- **Language**: Java 17+  
- **Framework**: [LangChain4j](https://github.com/langchain4j/langchain4j)  
- **Vector DB**: [Milvus / Zilliz](https://zilliz.com/)  
- **LLM Provider**: [Ollama](https://ollama.ai/) (running models like `llama3` locally)  

**Core Pipeline:**
1. Chunk knowledge documents  
2. Store embeddings in Milvus  
3. Perform semantic search  
4. Generate answers with context retrieved from the vector DB  

---

## 🧩 Architecture

```mermaid
flowchart TD
    A[📖 Knowledge Base: knowledge.txt] -->|🔹 Chunk + Embed| B[(🗄️ Milvus Vector DB)]
    Q[❓ Questions: questions.txt] -->|🔍 Search Embeddings| B
    B -->|📑 Top-K Context| LLM[🤖 Ollama LLM (llama3)]
    LLM -->|📝 Generated Answer| OUT[answers.txt + report.csv]
````

---

## ✨ Features

* ✅ Smart **chunking** of knowledge text for efficient semantic search
* ✅ **Vector storage & retrieval** with Milvus (Cloud or Local)
* ✅ **LLM integration** with Ollama for natural answers
* ✅ **Reports**:

  * Answers → `answers.txt`
  * Performance stats → `report.csv`

---

## 📦 Installation

### 1️⃣ Clone the repository

```bash
git clone https://github.com/yousseifmustafa/semantic-search-rag-java.git
cd semantic-search-rag-java
```

### 2️⃣ Setup Environment

* Install [Ollama](https://ollama.ai/) and run a model:

  ```bash
  ollama run llama3:8b
  ```

* Create an account on [Zilliz Cloud](https://zilliz.com/) and obtain:

  * **URI**
  * **TOKEN**

* Create a `.env` file in the project root:

  ```env
  MILVUS_URI=your-milvus-uri
  MILVUS_TOKEN=your-milvus-token
  ```

⚠️ `.env` is already ignored in `.gitignore` so it will never be pushed to GitHub.

---

## 🚀 Running the Project

### Build & Run

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Input / Output Files

* 📖 `knowledge.txt` → knowledge base text chunks
* ❓ `questions.txt` → questions to be answered
* 📝 `answers.txt` → generated answers
* 📊 `report.csv` → detailed report (retrieval/generation times, context size, etc.)

---

## 📂 Project Structure

```
semantic-search-rag-java/
│── src/main/java/com/example/Main.java    # main code
│── knowledge.txt                          # knowledge base file
│── questions.txt                          # list of questions
│── answers.txt                            # generated answers (output)
│── report.csv                             # performance report
│── .env                                   # secrets (ignored by Git)
│── pom.xml                                # Maven dependencies & build config
```

---

## 🌟 Roadmap

* [ ] Support multiple knowledge sources (PDF, Docs, Databases)
* [ ] Web-based interface for interactive Q\&A
* [ ] Plug-in multiple LLM providers (OpenAI, Anthropic, etc.)
* [ ] Enhanced chunking & embedding strategies

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit changes and open a Pull Request

---

## 📜 License

MIT License © 2025 — Created with ❤️ by [yousseifmustafa](https://github.com/yousseifmustafa)

