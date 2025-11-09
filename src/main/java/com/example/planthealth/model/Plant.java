package com.example.planthealth.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plant {
    private Long id;
    private String name;
    private String type;
    private String imagePath;

    // AI DISEASE DETECTION FIELDS
    private String disease;
    private Double confidence;
    private Boolean healthy;

    // Optional: Helper methods
    public boolean isPlantHealthy() {
        return healthy != null && healthy;
    }

    public String getConfidencePercentage() {
        return confidence != null ? String.format("%.2f%%", confidence * 100) : "N/A";
    }
}