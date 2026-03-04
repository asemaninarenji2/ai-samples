package com.mainapp.openai.integration.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tavily-web-search", url = "https://api.tavily.com/search")
public interface TavilyWebSearchClient {
    
    @PostMapping
    TavilySearchResponse search(@RequestBody TavilySearchRequest request);
}
