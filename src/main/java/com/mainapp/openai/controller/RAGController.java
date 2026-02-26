package com.mainapp.openai.controller;

import com.mainapp.openai.essentials.customadvisor.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/rag")
public class RAGController {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    @Value("classpath:/prompttemplate/systemPromptRandomDataTemplate.st")
    Resource systemTemplate;//org.springframework.core.io.Resource;

    @Value("classpath:/prompttemplate/systemPromptTemplateForPdfRag.st")
    Resource hrSystemTemplate;//org.springframework.core.io.Resource;

    public RAGController(VectorStore vectorStore,
                         @Qualifier("chatClientWithMemory") ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    @GetMapping("/random/data")
    public ResponseEntity<String> randomData(@RequestHeader("username") String username,
            @RequestParam("message") String message) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)//consider docs which have probability of min 50% , 1 exact match
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String answer = chatClient.prompt().system(promptSystemSpec ->
                promptSystemSpec.text(systemTemplate).param("documents", similarContext))
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);

    }

    @GetMapping("/pdf/data")
    public ResponseEntity<String> pdfData(@RequestHeader("username") String username,
                                             @RequestParam("message") String message) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)//consider docs which have probability of min 50% , 1 exact match
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        String answer = chatClient.prompt().system(promptSystemSpec ->
                        promptSystemSpec.text(hrSystemTemplate).param("documents", similarContext))
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);

    }

}
