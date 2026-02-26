package com.mainapp.openai.rag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingConfig {
    //VectorStore injects (QdrantClient, EmbeddingModel)
    @Primary
    @Bean
    public EmbeddingModel primaryEmbeddingModel(
            @Qualifier("openAiEmbeddingModel") EmbeddingModel openAiEmbeddingModel) {
        return openAiEmbeddingModel;
    }
    // WHY CONFIG IS REQUIRED for multi model applications:
//    --> Spring Boot auto-configuration creates:
//    @Bean("openAiEmbeddingModel")
//    public EmbeddingModel openAiEmbeddingModel() { ... }
//
//    @Bean("ollamaEmbeddingModel")
//    public EmbeddingModel ollamaEmbeddingModel() { ... }
//
//    // VectorStore needs:
//    public VectorStore vectorStore(EmbeddingModel embeddingModel) { ... }
//    VectorStore asks for EmbeddingModel (no qualifier)
//    Confusion! Which one should Spring inject?
    //we mark one as primary here
    // OR spring.ai.vectorstore.qdrant.embedding-model: openAiEmbeddingModel  # or ollamaEmbeddingModel

//    You need 2 vector stores in multi-model scenarios when:
//
//1. Different Embedding Dimensions
// -java
// OpenAI: 1536 dimensions
// Ollama: 768 dimensions
// Can't store in same collection - vectors are incompatible sizes
//2. Separate Knowledge Domains
//            java
// VectorStore1: Technical documentation (OpenAI embeddings)
// VectorStore2: Customer support data (Ollama embeddings)
// Different contexts, different models
//3. Cost/Performance Optimization
// -java
// VectorStore1: High-quality embeddings (OpenAI) for critical queries
// VectorStore2: Fast/cheap embeddings (Ollama) for quick searches
//4. Model-Specific Data
// -java
// Each model works best with its own embedding style
// OpenAI-trained content → OpenAI vector store
// Ollama-trained content → Ollama vector store

}
