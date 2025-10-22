package com.mainapp.openai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    //FIRST STYLE OF CREATING A CHAT CLIENT
    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel){
        return ChatClient.create(openAiChatModel);
    }
    //SECOND STYLE - MORE CONTROLE TO DICTATE HOW CLIENT SHOULD WORK

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        ChatClient.Builder builder = ChatClient.builder(ollamaChatModel);
        return builder.build();
    }
}
