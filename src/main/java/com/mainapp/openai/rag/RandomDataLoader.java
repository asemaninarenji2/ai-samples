package com.mainapp.openai.rag;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class RandomDataLoader {
    private final VectorStore vectorStore;

    @PostConstruct
    public void loadSentencesIntoVectorStore(){
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
        List<Document> documents = new ArrayList<>();
        try {
            log.info("Starting to load {} sentences into vector store", sentences.size());
            sentences.forEach(sentence -> documents.add(new Document(sentence)));
            log.info("Created {} documents", documents.size());
            log.info("First document: {}", documents.get(0).getText());
            vectorStore.add(documents);
            log.info("Successfully loaded {} documents into vector store", documents.size());
        } catch (Exception e) {
            log.error("Failed to load sentences into vector store", e);
            // Don't re-throw to prevent application startup failure
        }
    }

}
