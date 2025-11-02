package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/stream")
public class StreamController {
    private final ChatClient chatClient;

    public StreamController(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/stream")
    public Flux<String> stream(@RequestParam("message")String message) {

        return chatClient.prompt()
                .user(message).stream().content();
    }
    //NOTE: stream method contrary to call method would not interrupt the thread it will return
    // a flux wrapper of string.
    //NOTE ON FLUX: see flux as a belt for which the response will be processed when the response is
    // completed at call.
//    FLUX VS. MONO
    //In the Spring Framework, particularly when dealing with reactive programming using Project Reactor,
    // `Flux` and `Mono` are two key types used to represent asynchronous sequences of data.
    //      1. **Mono**: - Represents a single asynchronous value or an empty result.
    //          It can emit either one value or no value (i.e., completion without a value).
    //          - Used when you expect a single result, such as retrieving a specific user from a database.
    //          Example: ```java Mono userMono = userService.findUserById(userId); ```
    //      2. **Flux**: - Represents a sequence of 0 to N asynchronous values.
    //          It can emit multiple values over time, or complete without emitting any values.
    //          - Used when you expect multiple results, such as listing all users.
    //          Example: ```java Flux userFlux = userService.findAllUsers(); ```
    // In summary, use `Mono` for single or no results and `Flux` for multiple results in reactive
    // programming with Spring.
}
