package uzumtech.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firebase.enabled", havingValue = "true")
public class FirebaseConfig {

    private static final String APP_NAME = "NotificationApp";
    private final AppProperties appProperties;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        log.info("Initializing Firebase Messaging...");

        try {
            Resource resource = appProperties.getFirebase().getLocation();

            if (!resource.exists()) {
                throw new IllegalStateException(
                    "Firebase configuration file not found at: " + resource.getDescription()
                );
            }

            // Read and log file info (without exposing secrets)
            log.info("Loading Firebase config from: {}", resource.getDescription());
            log.info("File readable: {}", resource.isReadable());

            // Create credentials with explicit scopes
            GoogleCredentials credentials;
            try (InputStream inputStream = resource.getInputStream()) {
                credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(Arrays.asList(
                        "https://www.googleapis.com/auth/firebase.messaging",
                        "https://www.googleapis.com/auth/cloud-platform"
                    ));
            }

            // Test credential refresh before proceeding
            log.info("Testing credential refresh...");
            credentials.refresh();
            log.info("✓ Credential refresh successful");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

            FirebaseApp firebaseApp = FirebaseApp.getApps().stream()
                .filter(app -> APP_NAME.equals(app.getName()))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Creating new FirebaseApp: '{}'", APP_NAME);
                    return FirebaseApp.initializeApp(options, APP_NAME);
                });

            FirebaseMessaging messaging = FirebaseMessaging.getInstance(firebaseApp);
            log.info("✓ Firebase Messaging initialized successfully");

            return messaging;

        } catch (IOException e) {
            log.error("✗ Failed to initialize Firebase - IOException: {}", e.getMessage(), e);
            throw new IllegalStateException("Firebase initialization failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("✗ Failed to initialize Firebase - Unexpected error: {}", e.getMessage(), e);
            throw new IllegalStateException("Firebase initialization failed", e);
        }
    }
}