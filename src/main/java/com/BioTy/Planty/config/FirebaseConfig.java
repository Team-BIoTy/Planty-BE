package com.BioTy.Planty.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${fcm.firebase_config_path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            Resource resource;
            if (firebaseConfigPath.startsWith("/") || firebaseConfigPath.startsWith("file:")) {
                resource = new FileSystemResource(firebaseConfigPath);
            } else {
                resource = new ClassPathResource(firebaseConfigPath);
            }

            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(resource.getInputStream());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .build();
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase 앱이 초기화되었습니다.");
            }
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(){
        return FirebaseMessaging.getInstance(FirebaseApp.getInstance());
    }
}
