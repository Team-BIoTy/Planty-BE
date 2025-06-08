package com.BioTy.Planty.controller;

import com.BioTy.Planty.config.SensorFetchScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final SensorFetchScheduler sensorFetchScheduler;

    @PostMapping("/run-sensor-fetch")
    public ResponseEntity<Void> runSensorFetch() {
        sensorFetchScheduler.fetchSensorDataEveryHour();
        return ResponseEntity.ok().build();
    }
}
