package com.mainapp.openai.integration.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
@Slf4j
@Configuration
public class TavilyClientConfig {
    
    @Value("${TAVILY_API_KEY}")
    private String tavilyApiKey;
    
    @Bean
    public RequestInterceptor tavilyAuthInterceptor() {
        return template -> {
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + tavilyApiKey);
        };
    }
}
