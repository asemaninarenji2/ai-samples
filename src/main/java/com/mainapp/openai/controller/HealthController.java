package com.mainapp.openai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthController {

    @GetMapping("/qdrant")
    public ResponseEntity<String> checkQdrantHealth() {
        try {
            URL url = new URL("http://localhost:6333/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                return ResponseEntity.ok("Qdrant is healthy");
            } else {
                return ResponseEntity.status(responseCode).body("Qdrant returned status: " + responseCode);
            }
        } catch (IOException e) {
            log.error("Failed to connect to Qdrant", e);
            return ResponseEntity.status(503).body("Qdrant is not available: " + e.getMessage());
        }
    }

    @PostMapping("/qdrant/collection")
    public ResponseEntity<String> createCollection() {
        try {
            URL url = new URL("http://localhost:6333/collections/eazybytes");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            String jsonBody = "{\"vectors\":{\"size\":1536,\"distance\":\"Cosine\"}}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                return ResponseEntity.ok("Collection 'eazybytes' created successfully");
            } else {
                return ResponseEntity.status(responseCode).body("Failed to create collection. Status: " + responseCode);
            }
        } catch (IOException e) {
            log.error("Failed to create collection", e);
            return ResponseEntity.status(503).body("Failed to create collection: " + e.getMessage());
        }
    }
}
