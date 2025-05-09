package com.BioTy.Planty.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdafruitClient {

    @Value("${adafruit.api.url}")
    private String baseUrl;

    @Value("${adafruit.api.username}")
    private String username;

    @Value("${adafruit.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public void sendCommand(String feedKey){
        String url = String.format("%s/api/v2/%s/feeds/%s", baseUrl, username, feedKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-AIO-Key", apiKey);

        Map<String, String> body = Map.of("value", "ON");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }

}
