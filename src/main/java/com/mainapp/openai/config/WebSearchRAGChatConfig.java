package com.mainapp.openai.config;

import com.mainapp.openai.essentials.customadvisor.TokenUsageAuditAdvisor;
import com.mainapp.openai.integration.feign.TavilySearchResponse;
import com.mainapp.openai.rag.config.WebSearchDocumentRetriever;
import com.mainapp.openai.service.TavilyWebSearchService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Configuration
public class WebSearchRAGChatConfig {
    private final TavilyWebSearchService tavilyWebSearchService;

    @Bean("webSearchRAGChatClient")
    public ChatClient webSearchRAGChatClient(OpenAiChatModel chatModel, ChatMemory chatMemory ){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        Advisor advisorLogger = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        //1-Concrete class implementation of DocumentRetriever interface
        DocumentRetriever webSearchDocumentRetriever = new WebSearchDocumentRetriever(tavilyWebSearchService, 5);
        //2-Functional style implementation of DocumentRetriever interface
        DocumentRetriever functionalWebSearchDocumentRetriever = webSearchDocumentRetriever(tavilyWebSearchService, 5);

        //you can use both above DocumentRetrievers but result has to be var type. tried with BaseAdvisor and RetrievalAugmentationAdvisor it doesn't work
        var webSearchDocumentAdvisor = RetrievalAugmentationAdvisor.builder().documentRetriever(functionalWebSearchDocumentRetriever::apply).build();

        return builder.defaultAdvisors(List.of(messageChatMemoryAdvisor, advisorLogger, tokenUsageAdvisor, webSearchDocumentAdvisor)).build();
    }

    public DocumentRetriever webSearchDocumentRetriever(TavilyWebSearchService tavilyWebSearchService, int resultLimit) {
        return query -> {
            String queryText = query.text();
            TavilySearchResponse response = tavilyWebSearchService.search(queryText, resultLimit);
            if (response == null) {
                return List.of(new Document("NULL RESPONSE FROM TAVILY"));
            }
            List<Document> documentList = new ArrayList<>();
            response.getResults().forEach(result -> {
                documentList.add(Document.builder()
                        .text(result.getContent())
                        .metadata("title", result.getTitle())
                        .metadata("url", result.getUrl())
                        .score(result.getScore()).build());
            });
            return documentList;
        };
    }

}
