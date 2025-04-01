package com.BioTy.Planty.service;

import com.BioTy.Planty.config.AdafruitApiProperties;
import com.BioTy.Planty.entity.SoilMoisture;
import com.BioTy.Planty.repository.SoilMoistureRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class AdafruitService {

    private final SoilMoistureRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AdafruitApiProperties apiProperties;

    public AdafruitService(SoilMoistureRepository repository, AdafruitApiProperties apiProperties) {
        this.repository = repository;
        this.apiProperties = apiProperties;
    }

    @Scheduled(fixedRate = 100000)
    public void fetchData() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-AIO-Key", apiProperties.getKey());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiProperties.getUrl(), HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> data = response.getBody();

                SoilMoisture soilMoisture = new SoilMoisture();
                soilMoisture.setFeedId(String.valueOf(data.get("id")));

                Object valueObj = data.get("last_value");
                if (valueObj != null) {
                    try {
                        soilMoisture.setValue(Double.parseDouble(valueObj.toString()));
                    } catch (NumberFormatException e) {
                        System.err.println("🚨 Invalid value format: " + valueObj);
                        return;
                    }
                }

                if (data.get("created_at") != null) {
                    ZonedDateTime createdAtUTC = ZonedDateTime.parse(data.get("created_at").toString(), DateTimeFormatter.ISO_DATE_TIME);
                    soilMoisture.setSensorCreatedAt(createdAtUTC.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                }

                if (data.get("updated_at") != null) {
                    ZonedDateTime utcDateTime = ZonedDateTime.parse(data.get("updated_at").toString(), DateTimeFormatter.ISO_DATE_TIME);
                    soilMoisture.setSensorUpdatedAt(utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
                }

                repository.save(soilMoisture);
                System.out.println("✅ Data saved: " + soilMoisture.getValue());
            } else {
                System.out.println("🚨 Failed to fetch data: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("🚨 Error fetching data: " + e.getMessage());
        }
    }
}
