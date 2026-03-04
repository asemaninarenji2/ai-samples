package com.mainapp.openai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
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

@Slf4j
@RestController
@RequestMapping("/rag")
public class RAGController {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ChatClient chatClientWithMemoryAndRetrievalAugmentation;
    private final ChatClient webSearchRAGChatClient;
    private final ChatClient chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG;

    @Value("classpath:/prompttemplate/systemPromptTemplateForPdfRag.st")
    Resource hrSystemTemplate;//org.springframework.core.io.Resource;

    public RAGController(VectorStore vectorStore,
                         @Qualifier("chatClientWithMemory") ChatClient chatClient,
                         @Qualifier("chatClientWithMemoryAndRetrievalAugmentation") ChatClient chatClientWithMemoryAndRetrievalAugmentation,
                         @Qualifier("webSearchRAGChatClient") ChatClient webSearchRAGChatClient,
                         @Qualifier("chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG") ChatClient chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
        this.chatClientWithMemoryAndRetrievalAugmentation = chatClientWithMemoryAndRetrievalAugmentation;
        this.webSearchRAGChatClient = webSearchRAGChatClient;

        this.chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG = chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG;
    }

    @GetMapping("/manual-search/data")
    public ResponseEntity<String> pdfData(@RequestHeader("username") String username,
                                             @RequestParam("message") String message) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)//consider docs which have probability of min 50% , 1 exact match
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocuments.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String answer = chatClient.prompt().system(promptSystemSpec ->
                        promptSystemSpec.text(hrSystemTemplate).param("documents", similarContext))
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }

    //NOTE: above method is using manual search in vector store. the below method uses Retrieval Augmentation Advisor
    @GetMapping("/advisor-search/data")
    public ResponseEntity<String> augmentedSearch(@RequestHeader("username") String username,
                                          @RequestParam("message") String message) {
        //NOTE: NO NEED for systemPrompt population like :system(promptSystemSpec ->
        //                        promptSystemSpec.text(hrSystemTemplate).param("documents", similarContext)
        // the advisor will look after it automatically
        String answer = chatClientWithMemoryAndRetrievalAugmentation.prompt().advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        log.info("Answer: {}", answer);
        return ResponseEntity.ok(answer);

    }

    // Note : above method is using Retrieval Augmentation Advisor search from Vector store , we can replace AND/OR with web search
    // to get more relevant results : user ---prompt--> |ChatClientWithMemoryAndRetrievalAugmentation|-----prompt + Context --> LLM ---> Response
    //                                                  |                                            |
    //                                                  |                                            |
    //                                                  |                                            |
    //                                                  V                                            |
    //                                                  --------------------Web search----------------                                            V
    //WHY DO WE NEED WEB SEARCH ?
    // Because each llm model has a knowledge cutoff date, so if we want to get more relevant results we can use web search to get the latest information
    // Wrapper apps like ChatGpt, Claude etc. use web search to get the latest information combined with their already trained data
    //We need to make some api calls to some endpoints like tavily, google etc. to get the latest information. We need API keys from tavily

    //Web Search ----------------------------->
    @GetMapping("/web-search/data")
    public ResponseEntity<String> getChatClient(@RequestHeader("username") String username,
                                                @RequestParam("message") String message) {
        String answer = webSearchRAGChatClient.prompt().advisors(a ->
                        a.param(CONVERSATION_ID, username)).user(message).call().content();
        return ResponseEntity.ok(answer);
    }
    //<----------------------------------------

    @GetMapping("/advisor-search/advanced-rag/data")
    public ResponseEntity<String> augmentedSearchAdvancedRag(@RequestHeader("username") String username,
                                                  @RequestParam("message") String message) {

        String answer = chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG.prompt().advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call().content();
        log.info("Answer: {}", answer);
        return ResponseEntity.ok(answer);

    }
}
