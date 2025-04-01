package com.BioTy.Planty.controller;

import com.BioTy.Planty.entity.SoilMoisture;
import com.BioTy.Planty.repository.SoilMoistureRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/soilmoisture")
public class SoilMoistureController {

    private final SoilMoistureRepository repository;

    public SoilMoistureController(SoilMoistureRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SoilMoisture> getAllData() {
        return repository.findAll();
    }

}
