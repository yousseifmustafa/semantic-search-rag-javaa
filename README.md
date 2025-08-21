
````markdown
# 🔍 Semantic Search RAG in Java

نظام **RAG (Retrieval Augmented Generation)** مكتوب بـ **Java**،  
بيجمع بين **Semantic Search** و **LLMs** عشان يجاوب على الأسئلة باستخدام قاعدة معرفة محلية أو على Milvus (Zilliz Cloud).

---

## ⚡ نظرة سريعة
- **اللغة**: Java 17+
- **الـ Framework**: [LangChain4j](https://github.com/langchain4j/langchain4j)
- **الـ Vector DB**: [Milvus / Zilliz](https://zilliz.com/)
- **LLM Provider**: [Ollama](https://ollama.ai/) (تشغيل نماذج زي `llama3` محليًا)
- **الوظيفة الأساسية**:  
  1. يقسم النصوص (chunking)  
  2. يخزنها كـ embeddings في Milvus  
  3. يعمل Semantic Search  
  4. يولد إجابة من الـ LLM بناءً على النصوص المسترجعة  

---

## 🧩 المعمارية (Architecture)

```mermaid
flowchart TD
    A[Knowledge Base: knowledge.txt] -->|Chunk + Embedding| B[Milvus Vector DB]
    Q[Question: questions.txt] -->|Embedding + Search| B
    B -->|Top-K Context| LLM[Ollama LLM (llama3)]
    LLM -->|Generated Answer| OUT[answers.txt + report.csv]
````

---

## ✨ Features

* ✅ **Chunking ذكي** للنصوص لتسهيل البحث الدلالي.
* ✅ **Semantic Search** باستخدام Milvus (Cloud أو Local).
* ✅ **LLM Integration** مع Ollama لتوليد إجابات طبيعية.
* ✅ **تقارير جاهزة**:

  * الإجابات → `answers.txt`
  * الإحصائيات (سرعة البحث + التوليد) → `report.csv`

---

## 📦 Installation

### 1️⃣ Clone المشروع

```bash
git clone https://github.com/yousseifmustafa/semantic-search-rag-java.git
cd semantic-search-rag-java
```

### 2️⃣ إعداد البيئة

* نزّل [Ollama](https://ollama.ai/) وشغّل نموذج:

  ```bash
  ollama run llama3:8b
  ```

* اعمل حساب على [Zilliz Cloud](https://zilliz.com/) وخد:

  * **URI**
  * **TOKEN**

* اعمل ملف `.env` في جذر المشروع:

  ```env
  MILVUS_URI=your-milvus-uri
  MILVUS_TOKEN=your-milvus-token
  ```

⚠️ الملف `.env` متضاف في `.gitignore` علشان ميترفعش.

---

## 🚀 Run

### Build & Run

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### ملفات الإدخال/الإخراج

* ✍️ `knowledge.txt` → النصوص اللي عايز تضيفها كمعرفة.
* ❓ `questions.txt` → الأسئلة اللي النظام هيجاوب عليها.
* 📄 `answers.txt` → الإجابات الناتجة.
* 📊 `report.csv` → تقرير تفصيلي عن كل إجابة.

---

## 📂 Project Structure

```
semantic-search-rag-java/
│── src/main/java/com/example/Main.java    # الكود الرئيسي
│── knowledge.txt                          # قاعدة المعرفة
│── questions.txt                          # الأسئلة
│── answers.txt                            # الإجابات (ناتج التشغيل)
│── report.csv                             # تقرير الأداء
│── .env                                   # بيانات سرية (متجاهل من Git)
│── pom.xml                                # إعدادات Maven
```

---

## 🌟 Roadmap

* [ ] دعم مصادر معرفة متعددة (PDF, Docs, DB)
* [ ] واجهة ويب بسيطة للـ Q\&A
* [ ] دعم أكثر من LLM (OpenAI, Anthropic)
* [ ] تحسين الـ Chunking والـ Embedding

---

## 🤝 المساهمة

لو حابب تطور المشروع:

1. اعمل Fork
2. اعمل فرع جديد `feature/my-feature`
3. ابعت Pull Request

---

## 📜 License

MIT License © 2025 — Created with ❤️ by [yousseifmustafa](https://github.com/yousseifmustafa)

```

