package com.mainapp.openai.controller;

import com.mainapp.openai.customadvisor.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/advisor")
public class AdviserController {



    //ADVISERS : User -> ChatClient -> [ADVISORS] -> LLM -> response -> [ADVISORS] -> USER
    //BEST PRACTICES:
    //  .Keep advisors stateless or request-scoped
    //  .Chain multiple advisors if needed
    //  . Avoid altering the meaning of prompts unless intentional
    //  . Use advisors for cross-cutting concerns, not core logic
    // TO IMPLEMENT: in the chatClient config add.defaultAdviser to the chatClientBuilder


    private final ChatClient chatClient;

    public AdviserController(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/advice")
    public String advisor(@RequestParam("message")String message) {
        TokenUsageAuditAdvisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        SafeGuardAdvisor safeGuardAdvisor = new SafeGuardAdvisor(List.of("password"));
        SimpleLoggerAdvisor simpleLoggerAdvisor = new SimpleLoggerAdvisor();
        List<Advisor> advisors = List.of(simpleLoggerAdvisor,safeGuardAdvisor, tokenUsageAuditAdvisor);
        return chatClient.prompt().advisors(advisors)
                .user(message).call().content();
    }

}
