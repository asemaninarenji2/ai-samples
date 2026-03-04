package com.mainapp.openai.rag.config;

import com.mainapp.openai.integration.feign.TavilySearchResponse;
import com.mainapp.openai.service.TavilyWebSearchService;
import lombok.Builder;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.ArrayList;
import java.util.List;

@Builder
public class WebSearchDocumentRetriever implements DocumentRetriever {
    private final TavilyWebSearchService tavilyWebSearchService;
    private static final int DEFAULT_RESULT_LIMIT = 5;
    private int resultLimit;

    public WebSearchDocumentRetriever(TavilyWebSearchService tavilyWebSearchService,
                                      int resultLimit) {
        this.tavilyWebSearchService = tavilyWebSearchService;
        this.resultLimit = resultLimit;
    }


    @Override
    public List<Document> retrieve(Query query) {
        String queryText = query.text();
        TavilySearchResponse response = tavilyWebSearchService.search(queryText, this.resultLimit);
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
    }

}
