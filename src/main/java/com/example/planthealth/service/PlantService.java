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

    public Map<String, Object> analyzePlantDisease(MultipartFile file) {
        try {
            System.out.println("SENDING FILE TO FLASK ");
            System.out.println("File: " + file.getOriginalFilename());
            System.out.println("Size: " + file.getSize());

            if (file.isEmpty()) {
                throw new RuntimeException("Uploaded file is empty");
            }
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            byte[] fileBytes = file.getBytes();
            org.springframework.core.io.ByteArrayResource fileResource = new org.springframework.core.io.ByteArrayResource(
                    fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", fileResource);
            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            System.out.println("Sending request to Flask...");
            ResponseEntity<Map> response = restTemplate.exchange(
                    mlApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class);

            System.out.println("Response status: " + response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = response.getBody();
                System.out.println("SUCCESS! Received: " + result);
                return result;
            } else {
                System.err.println("Flask error: " + response.getStatusCode());
                throw new RuntimeException("Flask returned: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Analysis failed: " + e.getMessage());
        }
    }
}