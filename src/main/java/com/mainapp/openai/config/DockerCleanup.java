package com.mainapp.openai.config;


import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.File;
@Slf4j
@Component
public class DockerCleanup {
    @PreDestroy
    public void cleanup() {
        try {
            // Run from project root directory where docker-compose.yml is located
            ProcessBuilder pb = new ProcessBuilder("docker-compose", "down", "-v");
            pb.directory(new File(System.getProperty("user.dir"))); // Set working directory
            pb.inheritIO(); // Show output
            Process process = pb.start();
            int exitCode = process.waitFor();
            log.info("Docker cleanup exit code: {}", exitCode);
        } catch (Exception e) {
            log.error("Failed to cleanup docker containers: {}", e.getMessage(), e);
        }
    }
}
