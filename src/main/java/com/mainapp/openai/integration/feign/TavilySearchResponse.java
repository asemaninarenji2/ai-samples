package com.mainapp.openai.integration.feign;

import lombok.Data;
import java.util.List;

@Data
public class TavilySearchResponse {
    private String answer;
    private List<TavilyResult> results;
    private int response_time;
    private String created_at;
    
    @Data
    public static class TavilyResult {
        private String title;
        private String url;
        private String content;
        private double score;
        private String raw_content;
        private String published_date;
    }
}
