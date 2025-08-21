

````markdown
# 🤖 LangChain4j + Zilliz Cloud + Ollama RAG

A **Java Retrieval-Augmented Generation (RAG)** pipeline using **LangChain4j**, **Ollama**, and **Zilliz Cloud (Milvus)**.  
It ingests your knowledge, stores embeddings in Zilliz Cloud, and answers natural language questions with context-aware responses.

---

## ✅ Requirements
- Java 17+
- Maven
- [Ollama](https://ollama.com) with models:
  - `llama3:8b`
  - `nomic-embed-text`
- [Zilliz Cloud](https://cloud.zilliz.com) account (URI + API Key)

---

## ⚙️ Setup

1. **Configure Zilliz**  
   - Get your `URI` and `API Key` from Zilliz Cloud  
   - Update `Main.java`:
     ```java
     String milvusUri = "YOUR_ZILLIZ_URI";
     String milvusToken = "YOUR_ZILLIZ_API_KEY";
     ```

2. **Prepare Knowledge**  
   - Add your text knowledge base to `knowledge.txt`  
   - Add your test questions to `questions.txt`

3. **Run Ollama locally**  
   ```bash
   ollama pull llama3:8b
   ollama pull nomic-embed-text
   ollama run llama3:8b
   ollama run nomic-embed-text
````

4. **Build & Run the Project**

   ```bash
   mvn clean package
   mvn exec:java -Dexec.mainClass="com.example.Main"
   ```

---

## 📁 Project Structure

```
RAG-TASK/
├── src/
│   └── main/java/com/example/Main.java   # Main RAG pipeline
├── pom.xml
├── knowledge.txt                         # Knowledge base input
├── questions.txt                         # Questions input
├── answers.txt                           # Generated answers (output)
├── report.csv                            # Evaluation metrics
└── README.md
```

---

## 📌 Example

**Input (`questions.txt`):**

```
What is the difference between Artificial Intelligence and Machine Learning?
```

**Output (`answers.txt`):**

```
Question:
What is the difference between Artificial Intelligence and Machine Learning?

Answer:
Artificial Intelligence (AI) refers to the broader field of computer science that develops methods and systems to perceive environments, reason, and act intelligently. 
Machine Learning (ML) is a subfield of AI focused on algorithms that allow computers to learn from data without explicit programming.

Stats: retrieval=48 ms, generation=301 ms, total=349 ms, ctx_chars=872, top_k=3
-----------------------------------------------------
```

**Evaluation Log (`report.csv`):**

```csv
timestamp,question,answer_length,top_k,ctx_chars,retrieval_ms,generation_ms,total_ms,manual_relevance
2025-08-22 16:05:33,"What is the difference between Artificial Intelligence and Machine Learning?",312,3,872,48,301,349,
```

---

## 📄 Features

* ✅ End-to-end **RAG pipeline** in Java
* ✅ Chunked ingestion of knowledge text
* ✅ Embeddings stored in **Zilliz Cloud (Milvus)**
* ✅ Context-aware answering via **Ollama (llama3:8b)**
* ✅ Performance metrics (retrieval, generation, total latency)
* ✅ Manual evaluation support with `report.csv`

---

## 👨‍💻 Developer

Developed by **Youssef** — Educational demo of building a full **RAG pipeline** with **Java**, **LangChain4j**, **Zilliz Cloud**, and **Ollama**.

---

## 🧠 Keywords

`LangChain4j` · `Java` · `RAG` · `Zilliz Cloud` · `Ollama` · `Milvus` · `Local LLMs`

