package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1")
public class MemoryChatClientController {
    private final ChatClient chatClient;

    public MemoryChatClientController(@Qualifier("chatClientWithMemory") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/memory")
    public ResponseEntity<String> memory(@RequestParam("message")String message) {

        return ResponseEntity.ok(chatClient.prompt()
                .user(message).call().content());
    }

    // In Order for chatClient remember the history of the chat.
    // -> ChatMemory interface ==> 1. InMemoryChatMemoryRepository 2.JdbcChatMemoryRepository

}
