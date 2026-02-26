package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@RestController
@RequestMapping("/memory")
public class MemoryChatClientController {
    private final ChatClient chatClient;

    public MemoryChatClientController(@Qualifier("chatClientWithMemory") ChatClient chatClientWithMemory) {
        this.chatClient = chatClientWithMemory;
    }

    @GetMapping("/default-conversation-id")
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

    @GetMapping("/unique-conversation-id")
    public ResponseEntity<String> memoryDifferentConversationId(@RequestHeader("username") String username,
            @RequestParam("message")String message) {

        return ResponseEntity.ok(chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                //NOTE THAT: in bean creation the bean already setting MessageChatMemoryAdvisor as advisor
                //here we only provide a unique id to advisor in advisor overloaded method
                // the labmda is calling param method and CONVERSATION_ID is from ChatMemory class
                //REMEMBER: Conversation with Chat memory is through
                // InMemoryChatMemoryRepository implements ChatMemoryRepository which has concurrentHashMap and all add delete methods
                //
                .call().content());
    }
    //TO Retain all the chat history even after restart of application , you should save the chats in DB
    // H2 in memory:
    //  - set these setting to save the h2 content in a file to be retrieved after restart
    //      spring.datasource.url=jdbc:h2:file:~/chatmemory; AUto_SERVER=true  <- save in file and retrieve after restart
    //      spring.datasource.driver-class-name=org.h2.driver
    //      spring.datasource.username=username
    //      spring.datasource.password=password

}
