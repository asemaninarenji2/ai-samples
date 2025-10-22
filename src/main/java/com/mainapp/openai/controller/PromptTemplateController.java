package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prompt")
public class PromptTemplateController {
    private final ChatClient chatClient;

    public PromptTemplateController(@Qualifier("openAiChatClient") ChatClient openAiChatClient) {
        this.chatClient = openAiChatClient;
    }
    @Value("classpath:/prompttemplate/emailPromptTemplate.st")
    Resource promptTemplate;
    @GetMapping("/email")
    public String prompt(@RequestParam("customerName") String name,
                         @RequestParam("customerMessage") String message){

        return chatClient.prompt().system("You are a professional customer service assistance who helps drafting " +
                "email responses to improve the productivity of the customer support team")
                .user(temp -> temp.text(promptTemplate)
                        .param("customerName", name)
                        .param("customerMessage", message))
                .call().
                content();

    }


}
