package com.example.planthealth.controller;

import com.example.planthealth.service.DiseaseService;
import com.example.planthealth.model.Plant;
import com.example.planthealth.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@Controller
public class PlantController {

    @Autowired
    private PlantService service;
    @Autowired
    private DiseaseService diseaseService;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @PostMapping("/api/plant/analyze")
    public String analyzePlant(@RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("image") MultipartFile image,
            Model model) {
        try {
            System.out.println(" CONTROLLER ");
            System.out.println("Calling Detection...");
            Map<String, Object> result = service.analyzePlantDisease(image);
            String disease = (String) result.get("disease");
            Plant plant = new Plant(); // default : Uploaded Plant
            plant.setName(name);
            plant.setType(type);
            plant.setDisease((String) result.get("disease"));
            plant.setConfidence((Double) result.get("confidence"));
            plant.setHealthy((Boolean) result.get("is_healthy"));

            System.out.println("SUCCESS: " + plant.getDisease() + " - " + plant.getConfidence());
            System.out.println("Getting treatment recommendations...");
            Map<String, Object> treatment = diseaseService.getTreatmentRecommendations(disease);
            System.out.println("Treatment data: " + treatment);
            model.addAttribute("plant", plant);
            model.addAttribute("disease", plant.getDisease());
            model.addAttribute("confidence", plant.getConfidence());
            model.addAttribute("isHealthy", plant.getHealthy());
            model.addAttribute("topPredictions", result.get("top_predictions"));
            model.addAttribute("treatment", treatment);
            model.addAttribute("severity", treatment.get("severity"));
            return "plant-result";

        } catch (Exception e) {
            System.err.println("CONTROLLER ERROR: " + e.getMessage());
            model.addAttribute("error", "AI Detection Failed: " + e.getMessage());
            return "index";
        }
    }
}