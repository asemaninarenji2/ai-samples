package com.mainapp.openai.integration.feign;

import lombok.Data;
import java.util.List;

@Data
public class TavilySearchRequest {
    private String api_key;
    private String query;
    private String search_depth;
    private Boolean include_answer;
    private Boolean include_raw_content;
    private int max_results;
    private List<String> include_domains;
    private List<String> exclude_domains;
}
