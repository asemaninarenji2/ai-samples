package com.mainapp.openai.service;

import com.mainapp.openai.integration.feign.TavilySearchRequest;
import com.mainapp.openai.integration.feign.TavilySearchResponse;
import com.mainapp.openai.integration.feign.TavilyWebSearchClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TavilyWebSearchService {
    

    private final TavilyWebSearchClient tavilySearchClient;
    
    public TavilySearchResponse search(String query, int resultLimit) {
        TavilySearchRequest request = new TavilySearchRequest();
        request.setQuery(query);
        request.setSearch_depth("advanced");
        request.setInclude_answer(false);
        request.setInclude_raw_content(false);
        request.setMax_results(resultLimit);
        
        return tavilySearchClient.search(request);
    }
}
