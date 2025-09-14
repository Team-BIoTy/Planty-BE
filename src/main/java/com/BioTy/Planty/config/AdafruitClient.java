package com.BioTy.Planty.config;

import com.BioTy.Planty.dto.iot.SensorLogResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AdafruitClient {

    @Value("${adafruit.api.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public SensorLogResponseDto fetchFeedInfo(String username, String apiKey, String feedKey){
        String url = String.format("%s/api/v2/%s/feeds/%s", baseUrl, username, feedKey);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-AIO-Key", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, SensorLogResponseDto.class).getBody();
    }

    public void sendCommand(String username, String apiKey, String feedKey, String value){
        String url = String.format("%s/api/v2/%s/feeds/%s/data", baseUrl, username, feedKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-AIO-Key", apiKey);

        Map<String, String> body = Map.of("value", value);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }

}
