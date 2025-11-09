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
        return "index"; // Your upload form
    }

    @PostMapping("/api/plant/analyze")
    public String analyzePlant(@RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("image") MultipartFile image,
            Model model) {
        try {
            System.out.println("=== ULTRA SIMPLE CONTROLLER ===");

            // 1. ONLY DO AI DISEASE DETECTION
            System.out.println("🔍 Calling AI Detection...");
            Map<String, Object> aiResult = service.analyzePlantDisease(image);
            String disease = (String) aiResult.get("disease"); // ADD THIS LINE
            // 2. Create plant with AI results
            Plant plant = new Plant();
            plant.setName(name);
            plant.setType(type);
            plant.setDisease((String) aiResult.get("disease"));
            plant.setConfidence((Double) aiResult.get("confidence"));
            plant.setHealthy((Boolean) aiResult.get("is_healthy"));

            System.out.println("✅ SUCCESS: " + plant.getDisease() + " - " + plant.getConfidence());
            // 2. CALL DISEASE SERVICE FOR TREATMENT - NEW!
            System.out.println("💊 Getting treatment recommendations...");
            Map<String, Object> treatment = diseaseService.getTreatmentRecommendations(disease);
            System.out.println("✅ Treatment data: " + treatment);

            // 3. Pass to template
            model.addAttribute("plant", plant);
            model.addAttribute("disease", plant.getDisease());
            model.addAttribute("confidence", plant.getConfidence());
            model.addAttribute("isHealthy", plant.getHealthy());
            model.addAttribute("topPredictions", aiResult.get("top_predictions"));
            // new
            model.addAttribute("treatment", treatment);
            model.addAttribute("severity", treatment.get("severity"));
            return "plant-result";

        } catch (Exception e) {
            System.err.println("❌ CONTROLLER ERROR: " + e.getMessage());
            model.addAttribute("error", "AI Detection Failed: " + e.getMessage());
            return "index";
        }
    }

    // KEEP YOUR EXISTING METHODS, JUST ADD THIS NEW ONE FOR DIRECT AI ANALYSIS
    @PostMapping("/analyze-plant")
    public String analyzePlantOnly(@RequestParam("plantImage") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select an image file");
                return "index";
            }

            Map<String, Object> aiResult = service.analyzePlantDisease(file);
            String disease = (String) aiResult.get("disease"); // ADD THIS LINE
            Map<String, Object> treatment = diseaseService.getTreatmentRecommendations(disease);

            // Create a basic plant object for the template
            Plant plant = new Plant();
            plant.setDisease((String) aiResult.get("disease"));
            plant.setConfidence((Double) aiResult.get("confidence"));
            plant.setHealthy((Boolean) aiResult.get("is_healthy"));

            model.addAttribute("plant", plant);
            model.addAttribute("aiResult", aiResult);

            // ADD SEPARATE ATTRIBUTES FOR THIS METHOD TOO
            model.addAttribute("disease", plant.getDisease());
            model.addAttribute("confidence", plant.getConfidence());
            model.addAttribute("isHealthy", plant.getHealthy());
            model.addAttribute("topPredictions", aiResult.get("top_predictions"));
            model.addAttribute("treatment", treatment);
            model.addAttribute("severity", treatment.get("severity"));
            return "plant-result";

        } catch (Exception e) {
            model.addAttribute("error", "Error analyzing image: " + e.getMessage());
            return "index";
        }
    }
}