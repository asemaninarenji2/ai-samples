package com.mainapp.openai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/options")
public class ChatOptionsController {
    private final ChatClient chatClient;

    public ChatOptionsController(@Qualifier("openAiChatClientWithOptions") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/use")
    public String chat(@RequestParam("message")String message){
        OpenAiChatOptions specificOtions = OpenAiChatOptions.builder().model(OpenAiApi.ChatModel.CHATGPT_4_O_LATEST).build();
        return chatClient.prompt().user(message)
                .advisors(new SimpleLoggerAdvisor()).options(specificOtions)
                .call().content();
    }
    //NOTE: The config bean is using generic options for all models
    //Here we intentionally add the chat options which is specific to openAi
    //NOTE: the options can also be added in the property files like application.yml
    //Good for multi env and switching based on need of the env
    //NOTE:
    //ChatOptions options = ChatOptions.builder()
    // .model().frequencyPenalty().presencePenalty()
    // .temperature().topK().topP().stopSequences().build();

    //HOW TO ADD TO CHAT CLIENT:
    //  1. at chatClientBuilder : applied on all chats -> chatClientBuilder.defaultOptions(options).build();
    //  2. at specific chat: applied on that chat only -> chatClient.prompt().options(options).user(msg).call().content(;


    //    OPTION        |            Meaning
    //--------------------------------------------------------------------------------------------
    //  Model           | Which LLM Model to Use (eg: gpt-4, gpt-3.5-turbo, etc)
    //  frequencyPenalty| Reduce repetition . Higher = less repetition
    //  PresencePenalty | Encourages mentioning new topics
    //  temperature     | Controls creativity(0 --1) 0= focused , 1 = random
    //  top p           | Controls randomness(nucleus sampling)
    //  stopSequence    | stop generation when specific phrases are found eg: nothing after "]}"
    //  maxTokens       | Controls how manu top choices are considered

    //IMPORTANT
    //  1.ChatOptions is generic interface(it defines all common options between all different models)
    //  2.OpenAiChatOptions: Concrete imp of ChatOptions(contains specific options for openAi only)

}
