package com.example.planthealth.service;

import com.example.planthealth.model.Plant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PlantService {

    private final String mlApiUrl = "http://127.0.0.1:5000/predict";

    /*
     * public Plant analyzePlant(Plant plant) {
     * plant.setHealthStatus("Using AI Detection");
     * plant.setConfidenceScore(0.0);
     * return plant;
     * }
     */
    // WORKING VERSION - Simple and reliable
    public Map<String, Object> analyzePlantDisease(MultipartFile file) {
        try {
            System.out.println("=== SENDING FILE TO FLASK ===");
            System.out.println("📁 File: " + file.getOriginalFilename());
            System.out.println("📁 Size: " + file.getSize());

            if (file.isEmpty()) {
                throw new RuntimeException("Uploaded file is empty");
            }

            // Create REST template
            RestTemplate restTemplate = new RestTemplate();

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create the multipart body - SIMPLE APPROACH
            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Convert MultipartFile to byte array and create a resource
            byte[] fileBytes = file.getBytes();
            org.springframework.core.io.ByteArrayResource fileResource = new org.springframework.core.io.ByteArrayResource(
                    fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", fileResource);

            // Create the request entity
            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            System.out.println("🔄 Sending request to Flask...");

            // Make the request
            ResponseEntity<Map> response = restTemplate.exchange(
                    mlApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class);

            System.out.println("✅ Response status: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = response.getBody();
                System.out.println("🎉 SUCCESS! Received: " + result);
                return result;
            } else {
                System.err.println("❌ Flask error: " + response.getStatusCode());
                throw new RuntimeException("Flask returned: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("AI analysis failed: " + e.getMessage());
        }
    }
}