package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt(message).call().content();

    }

    //NOTE ON CONTENT METHOD:
    // the method is user friendly method but there are other methods to use too;
//---------------------------------------------------------------------------------------------------
//        METHOD            |               Return type            |                Use case
//---------------------------------------------------------------------------------------------------
//    content()             |   Just the response as a String      |    Simple use case - display or print reply
//  chatResponse()          |   A ChatResponse Obj                 |    Get full details like token usage
//  ChatClientResponse(0    |   A ChatClientResponse Obj           |    Useful in RAG- Includes contexty &  Metadata
//  entity(0                |   Converts response to POJO          |    Getting Java Objects (Structured Output




}
