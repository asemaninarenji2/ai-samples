package com.mainapp.openai.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class VectorDbDataLoader {
    private final VectorStore vectorStore;
    private final QdrantClient qdrantClient;

    @Value("${loader.random-data-loader}")
    private boolean loadRandomData;

    @Value("${loader.pdf-data-loader}")
    private boolean loadPdfData;

    @Value("classpath:hr.pdf")
    Resource hrPdf;

    @PostConstruct
    public void setLoadPdfData() {
        if (!loadPdfData) {
            log.info("No qdrant pdf loading required!");
            return;
        }
        try {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(hrPdf);
            List<Document> document = tikaDocumentReader.get();
            //NOTE: possible to embed the whole big pdf into one document but it will use huge number of tokens with each query
            //Instead using text splitter to split the pdf into smaller chunks like below.
            TextSplitter textSplitter = TokenTextSplitter.builder().withChunkSize(100).withMaxNumChunks(400).build();
            vectorStore.add(textSplitter.split(document));
        } catch (Exception e) {
            log.error("Failed to load pdf into vector store", e);
        }


    }

    @PostConstruct
    public void loadSentencesIntoVectorStore(){
        if (!loadRandomData) {
            log.info("No qdrant  random data loading required!");
            return;
        }

        List<String> sentences = List.of(
                "The sun rises in the east and sets in the west.",
                "Machine learning algorithms can process large datasets efficiently.",
                "Coffee is the most popular beverage in the world.",
                "The human brain contains approximately 86 billion neurons.",
                "Climate change affects global weather patterns significantly.",
                "Python is a versatile programming language for data science.",
                "The Great Wall of China is visible from space.",
                "Artificial intelligence is transforming various industries.",
                "Water covers about 71% of Earth's surface.",
                "The internet connects billions of devices worldwide.",
                "Photosynthesis converts sunlight into chemical energy.",
                "Quantum computing could revolutionize cryptography.",
                "The human heart beats about 100,000 times per day.",
                "Social media platforms have changed communication patterns.",
                "Renewable energy sources include solar and wind power.",
                "The Milky Way galaxy contains over 200 billion stars.",
                "Blockchain technology enables secure digital transactions.",
                "Exercise improves both physical and mental health.",
                "The Amazon rainforest produces 20% of world's oxygen.",
                "Cloud computing provides scalable infrastructure solutions.",
                "The human body has 206 bones in adulthood.",
                "Neural networks mimic the brain's learning process.",
                "Space exploration has expanded our understanding of the universe.",
                "Mobile devices have become essential for daily life.",
                "Genetic engineering can modify DNA sequences.",
                "The ocean contains 97% of Earth's water.",
                "Virtual reality creates immersive digital experiences.",
                "Automation is reshaping manufacturing processes.",
                "The Eiffel Tower was built for the 1889 World's Fair.",
                "Big data analytics helps businesses make informed decisions."
        );
        try {
            createCollectionIfNotExists();

            List<Document> documents = sentences.stream().map(Document::new).toList();
            vectorStore.add(documents);
            log.info("Successfully loaded {} documents into vector store", documents.size());
        } catch (Exception e) {
            log.error("Failed to load sentences into vector store", e);
            // Don't re-throw to prevent application startup failure
        }

    }

    private void createCollectionIfNotExists() {
        try {
            String collectionName = "eazybytes"; // Match application.yml

            // Check if collection exists
            var collectionNames = qdrantClient.listCollectionsAsync().get();
            boolean collectionExists = collectionNames.contains(collectionName);

            if (!collectionExists) {
                log.info("Creating collection: {}", collectionName);

                // Create collection with correct configuration
                var vectorParams = Collections.VectorParams.newBuilder()
                        .setSize(1536) // Match embedding-dimension from application.yml
                        .setDistance(Collections.Distance.Cosine)
                        .build();

                var createCollectionRequest = Collections.CreateCollection.newBuilder()
                        .setCollectionName(collectionName)
                        .setVectorsConfig(Collections.VectorsConfig.newBuilder()
                                .setParams(vectorParams)
                                .build())
                        .build();

                qdrantClient.createCollectionAsync(createCollectionRequest).get();
                log.info("Successfully created collection: {}", collectionName);
            } else {
                log.info("Collection {} already exists", collectionName);
            }
        } catch (Exception e) {
            log.error("Failed to create collection", e);
            throw new RuntimeException("Failed to create Qdrant collection", e);
        }
    }

}
