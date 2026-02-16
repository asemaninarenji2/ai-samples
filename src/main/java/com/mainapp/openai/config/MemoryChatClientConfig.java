package com.mainapp.openai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
public class MemoryChatClientConfig {

    @Bean("limitedMaxMessages")
    public ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }
    @Bean("chatClientWithMemory")
    public ChatClient chatClientWithMemory(OpenAiChatModel chatModel, @Qualifier("limitedMaxMessages") ChatMemory chatMemory){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        Advisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor advisorLogger = new SimpleLoggerAdvisor();
        return builder.defaultAdvisors(List.of(advisor, advisorLogger)).build();
    }

}
