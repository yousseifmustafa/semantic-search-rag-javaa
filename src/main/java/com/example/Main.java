package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;

public class Main {

    static final int CHUNK_SIZE_WORDS = 150;  
    static final int TOP_K = 3;               
    static final String KNOWLEDGE_PATH = "knowledge.txt";
    static final String QUESTIONS_PATH = "questions.txt";
    static final String ANSWERS_PATH = "answers.txt";
    static final String REPORT_CSV_PATH = "report.csv";
    static final boolean TRUNCATE_OUTPUTS_ON_START = true;
    public static void main(String[] args) {
        try {


            ChatLanguageModel llm = OllamaChatModel.builder()
                    .baseUrl( "http://localhost:11434")
                    .modelName("llama3:8b")
                    .timeout(java.time.Duration.ofMinutes(30))
                    .build();

            EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                    .baseUrl( "http://localhost:11434")
                    .modelName("nomic-embed-text")
                    .build();


            String milvusUri = "https://in03-29eca0c584bdfb8.serverless.aws-eu-central-1.cloud.zilliz.com";
            String milvusToken ="f5feb53b793c9984804c412ad36cb8eeb15d3f3cac15e2ca507085f3d034ed98cb720ef0b07f4a7fb699395a083d7f8ba26f3159";

            MilvusEmbeddingStore vectorDB = MilvusEmbeddingStore.builder()
                    .uri(milvusUri)
                    .token(milvusToken)
                    .collectionName("knowledge_base")
                    .dimension(768)
                    .build();

            if (TRUNCATE_OUTPUTS_ON_START) {
                Files.writeString(Path.of(ANSWERS_PATH), "",
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
              
                String header = "timestamp,question,answer_length,top_k,ctx_chars,retrieval_ms,generation_ms,total_ms,manual_relevance\n";
                Files.writeString(Path.of(REPORT_CSV_PATH), header,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }

            String knowledgeText = Files.readString(Path.of(KNOWLEDGE_PATH)).trim();
            if (knowledgeText.isEmpty()) {
                throw new IllegalStateException("Knowledge file is empty!");
            }

            String[] words = knowledgeText.split("\\s+");
            StringBuilder chunk = new StringBuilder();
            int count = 0;
            for (String word : words) {
                chunk.append(word).append(" ");
                count++;
                if (count >= CHUNK_SIZE_WORDS) {
                    addChunkToMilvus(vectorDB, embeddingModel, chunk.toString().trim());
                    chunk.setLength(0);
                    count = 0;
                }
            }
            if (chunk.length() > 0) {
                addChunkToMilvus(vectorDB, embeddingModel, chunk.toString().trim());
            }

            System.out.println("Knowledge stored in Milvus with chunking" );

            List<String> questions = Files.readAllLines(Path.of(QUESTIONS_PATH))
                    .stream()
                    .map(String::trim)
                    .filter(q -> !q.isEmpty())
                    .toList();

            try (BufferedWriter answers = new BufferedWriter(new FileWriter(ANSWERS_PATH, true));
                 PrintWriter report = new PrintWriter(new FileWriter(REPORT_CSV_PATH, true))) {

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (String userQuery : questions) {
                    String timestamp = LocalDateTime.now().format(fmt);

                    try {
                        long t0 = System.currentTimeMillis();
                        var queryEmbedding = embeddingModel.embed(userQuery).content();

                        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                                .queryEmbedding(queryEmbedding)
                                .maxResults(TOP_K)
                                .build();

                        EmbeddingSearchResult<TextSegment> searchResult = vectorDB.search(request);
                        List<EmbeddingMatch<TextSegment>> topResults = searchResult.matches();

                        long t1 = System.currentTimeMillis();
                        StringBuilder context = new StringBuilder();
                        for (EmbeddingMatch<TextSegment> match : topResults) {
                            context.append(match.embedded().text()).append("\n");
                        }
                        int ctxChars = context.length();

                        String prompt = String.format(
                                "You are a knowledgeable assistant. Answer only using the following information:\n%s\n\nQuestion: %s\nAnswer:",
                                context.toString(), userQuery);

                        String finalAnswer = llm.generate(prompt);
                        long t2 = System.currentTimeMillis();

                        long retrievalMs = (t1 - t0);
                        long generationMs = (t2 - t1);
                        long totalMs = (t2 - t0);

                        answers.write("Question:\n" + userQuery + "\n");
                        answers.write("Answer:\n" + finalAnswer.trim() + "\n");
                        answers.write("Stats: retrieval=" + retrievalMs + " ms, generation=" + generationMs
                                + " ms, total=" + totalMs + " ms, ctx_chars=" + ctxChars + ", top_k=" + TOP_K + "\n");
                        answers.write("-----------------------------------------------------\n");
                        answers.flush();

                        String csvRow = escapeCsv(timestamp) + "," +
                                escapeCsv(userQuery) + "," +
                                finalAnswer.trim().length() + "," +
                                TOP_K + "," +
                                ctxChars + "," +
                                retrievalMs + "," +
                                generationMs + "," +
                                totalMs + "," +
                                ""; 
                                  report.println(csvRow);
                        report.flush();

                        System.out.println("Done: " + userQuery + "  [" + totalMs + " ms]");

                    } catch (Exception perQuestionEx) {
                        String errMsg = "ERROR while processing question: " + userQuery + " -> " + perQuestionEx.getMessage();
                        answers.write(errMsg + "\n");
                        answers.write("-----------------------------------------------------\n");
                        answers.flush();

                        String csvRow = escapeCsv(timestamp) + "," +
                                escapeCsv(userQuery) + "," +
                                "0" + "," + TOP_K + "," + "0" + "," + "0" + "," + "0" + "," + "0" + "," + "ERROR";
                        report.println(csvRow);
                        report.flush();
                    }
                }
            }

            System.out.println("Answers saved to " + ANSWERS_PATH + " and report to " + REPORT_CSV_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addChunkToMilvus(MilvusEmbeddingStore vectorDB, EmbeddingModel embeddingModel, String text) {
        if (text == null || text.isBlank()) return;
        TextSegment segment = TextSegment.from(text);
        vectorDB.add(embeddingModel.embed(segment.text()).content(), segment);
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }
}
