package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class PromptStuffingController {
    private final ChatClient chatClient;

    @Value("classpath:/prompttemplate/systemPromptTemplate.st")
    Resource systemPromptTemplate;

    public PromptStuffingController(@Qualifier("openAiChatClient") ChatClient openAiChatClient) {
        this.chatClient = openAiChatClient;
    }

    @GetMapping("/prompt-stuffing")
    public String promptStuffing(@RequestParam("message")String message){
        return chatClient.prompt()
                .system(systemPromptTemplate)
                .user(message).call().content();
        //NOTE: USE stuffing for a few line of info(You will burn huge amount of tokens)
        //FOR bigger amount of data feed use RAG

    }
}
