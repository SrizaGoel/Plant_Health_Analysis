package com.example.planthealth.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiseaseService {
    
    public Map<String, Object> getTreatmentRecommendations(String disease) {
        Map<String, String> recommendations = new HashMap<>();
        
        // Disease-specific recommendations
        if (disease.toLowerCase().contains("early_blight")) {
            recommendations.put("organic", "Apply copper-based fungicide weekly. Remove infected leaves.");
            recommendations.put("chemical", "Use Chlorothalonil or Mancozeb fungicides");
            recommendations.put("prevention", "Rotate crops, improve air circulation, avoid overhead watering");
            recommendations.put("timeline", "Treat immediately, repeat every 7-10 days");
        }
        else if (disease.toLowerCase().contains("late_blight")) {
            recommendations.put("organic", "Remove infected leaves immediately. Apply baking soda spray.");
            recommendations.put("chemical", "Use fungicides containing Fosetyl-Al or Metalaxyl");
            recommendations.put("prevention", "Avoid overhead watering, space plants properly, use resistant varieties");
            recommendations.put("timeline", "URGENT: Treat within 24-48 hours");
        }
        else if (disease.toLowerCase().contains("bacterial_spot")) {
            recommendations.put("organic", "Apply copper spray, remove affected plants");
            recommendations.put("chemical", "Use copper-based bactericides");
            recommendations.put("prevention", "Use disease-free seeds, avoid working with wet plants");
            recommendations.put("timeline", "Start treatment immediately");
        }
        else if (disease.toLowerCase().contains("healthy")) {
            recommendations.put("maintenance", "Continue current care routine");
            recommendations.put("prevention", "Regular inspection, proper watering, balanced fertilization");
            recommendations.put("tips", "Monitor for early signs of stress, maintain good air circulation");
        }
        else {
            recommendations.put("general", "Consult local agricultural expert for specific diagnosis");
            recommendations.put("immediate", "Isolate plant to prevent disease spread");
            recommendations.put("research", "Identify specific disease characteristics for targeted treatment");
        }
        
        return Map.of(
            "disease", disease,
            "recommendations", recommendations,
            "severity", getDiseaseSeverity(disease)
        );
    }
    
    private String getDiseaseSeverity(String disease) {
        if (disease.toLowerCase().contains("healthy")) return "None";
        if (disease.toLowerCase().contains("blight") || disease.toLowerCase().contains("virus")) return "High";
        if (disease.toLowerCase().contains("spot") || disease.toLowerCase().contains("mold")) return "Medium";
        return "Low";
    }
}