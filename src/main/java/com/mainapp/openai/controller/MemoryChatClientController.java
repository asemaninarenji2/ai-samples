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
    // -> MessageWindowChatMemory contains get, add , clear etc methods to save previous messages in
    // ChatMemory classes and then with use of MessageChatMemoryAdvisor we can use old messages in chat(FULL CHAT including user, system assistance messages)
    // ChatMemory advisors:
        //1.MessageChatMemoryAdvisor: above
    //  //2.PromptChatMemoryAdvisor :  converts memory into plain text format, appends it to the system prompt lime a summery , good for simple LLMs oe when token budget is limited
    //  //3.VectorStorChatMemoryAdvisor : for using message of very long time say 3 month ago
//            It stores databases like Qdrant, Pinecone , then retrives the most relevant past messages using embeddings
//            ideal for long or knowledge-based conversations( semantic memory)

}
