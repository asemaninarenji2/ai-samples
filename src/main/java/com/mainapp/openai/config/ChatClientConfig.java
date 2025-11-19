package com.mainapp.openai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {
    //FIRST STYLE OF CREATING A CHAT CLIENT
    @Bean("ollamaChatClient")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.create(ollamaChatModel);
    }


    //SECOND STYLE - MORE CONTROLE TO DICTATE HOW CLIENT SHOULD WORK
    @Bean("openAiChatClient")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel){
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel);
        builder.defaultSystem("You are a professional IT desk support");
        return builder.build();
    }
    @Bean("openAiChatClientWithOptions")
    public ChatClient openAiChatClientWithOptions(OpenAiChatModel openAiChatModel){
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-4.1-mini").maxTokens(200).temperature(0.8).build();
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel);
        builder.defaultSystem("You are an knowing oracle");
        builder.defaultOptions(chatOptions);
        return builder.build();
    }
    @Bean("chatClientWithMemory")
    public ChatClient chatClientWithMemory(OpenAiChatModel chatModel, ChatMemory chatMemory){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        Advisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor advisorLogger = new SimpleLoggerAdvisor();
        return builder.defaultAdvisors(List.of(advisor, advisorLogger)).build();
    }



}
