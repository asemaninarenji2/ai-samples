package com.mainapp.openai.config;

import com.mainapp.openai.essentials.customadvisor.TokenUsageAuditAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
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
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        return builder.defaultAdvisors(List.of(advisor, advisorLogger, tokenUsageAdvisor)).build();
    }

    //Retrieval Augmentation Advisor beans definition ---------------->
    @Bean
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore){
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .build();

    }
    // (+)                -------------------------------------------
    @Bean("chatClientWithMemoryAndRetrievalAugmentation")
    public ChatClient chatClientWithMemoryAndRetrievalAugmentation(OpenAiChatModel chatModel,
                                                                   @Qualifier("limitedMaxMessages") ChatMemory chatMemory,
                                                                   RetrievalAugmentationAdvisor retrievalAugmentationAdvisor){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        Advisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor advisorLogger = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        return builder.defaultAdvisors(List.of(advisor, advisorLogger, tokenUsageAdvisor, retrievalAugmentationAdvisor)).build();
    }
    //<-----------------------------------------------------------------
    //ADVANCED RAG - for more effective retrieval tasks, addressing challenges such as poor formed queries,
    // ambiguous tems, complex vocabulary, unsupported languages etc
    //Naive RAG:  Retrieve --> Augmentation --> Generation
    //Advanced RAG:  Pre-retrieval ---> Retrieve ---> Post-retrieval --> Augmentation --> Generation
    @Bean("retrievalAugmentationAdvisorAdvancedRAG")
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisorAdvancedRAG(VectorStore vectorStore,
                                                                                @Qualifier("openAiChatModel") ChatModel chatModel){
        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel);
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(TranslationQueryTransformer.builder()// This triggers PRE_RETRIEVAL process by calling to LLM
                        .chatClientBuilder(chatClientBuilder.clone())   // hence you need to give it the chat client builder
                        .targetLanguage("english")                      //There are multiple QueryTransformer implementations:
                        .build())                                       //CompressionQueryTransformer, TranslationQueryTransformer, RewriteQueryTransformer
                                                                        // Refer to documentation of each for use cases.
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore)// Here search would be based on pre-retrieved documents
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .build();

    }
    @Bean("chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG")
    public ChatClient chatClientWithMemoryAndRetrievalAugmentationAndAdvancedRAG(OpenAiChatModel chatModel,
                                                                   @Qualifier("limitedMaxMessages") ChatMemory chatMemory,
                                                                   @Qualifier("retrievalAugmentationAdvisorAdvancedRAG") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        Advisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor advisorLogger = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        return builder.defaultAdvisors(List.of(advisor, advisorLogger, tokenUsageAdvisor, retrievalAugmentationAdvisor)).build();
    }

}
