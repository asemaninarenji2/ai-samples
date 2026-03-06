package com.mainapp.openai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Basic Chat", description = "Basic chat endpoints using OpenAI")
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    @Operation(summary = "Simple chat with OpenAI", description = "Basic chat interaction with OpenAI model")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "400", description = "Invalid message parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String chat(
            @Parameter(description = "User message to send to AI", required = true, example = "Hello, how are you?")
            @RequestParam("message") String message) {
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
