package com.CodeLab.Code_Execution_Service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String API_KEY;

    @Value("${gemini.api.url}")
    private String GEMINI_URL;

    public Map<String, String> analyzeCodeComplexity(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = "Analyze the time and space complexity of the following code. " + "Consider the time complexity based on the number of operations required to complete the task, " + "and the space complexity based on the extra memory required for the algorithm, excluding the input data. " + "Provide only the time complexity and space complexity in the format 'TC: O(...)' and 'SC: O(...)'. Do not include any other details or explanation.\n\n" + code;

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        Map<String, String> result = new HashMap<>();
        result.put("TC", "");
        result.put("SC", "");

        try {
            String fullUrl = GEMINI_URL + "?key=" + API_KEY; // Use this only if key is expected as query param
            ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentObj = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentObj.get("parts");

                    if (parts != null && !parts.isEmpty()) {
                        String text = parts.get(0).get("text").toString();

                        // Flexible regex (case-insensitive, supports variations)
                        Matcher tcMatcher = Pattern.compile("(?i)TC\\s*[:\\-]?\\s*O\\([^)]*\\)").matcher(text);
                        Matcher scMatcher = Pattern.compile("(?i)SC\\s*[:\\-]?\\s*O\\([^)]*\\)").matcher(text);

                        if (tcMatcher.find()) {
                            result.put("TC", tcMatcher.group().replaceAll("(?i)TC\\s*[:\\-]?\\s*", ""));
                        }
                        if (scMatcher.find()) {
                            result.put("SC", scMatcher.group().replaceAll("(?i)SC\\s*[:\\-]?\\s*", ""));
                        }
                    }
                }
            }
        } catch (Exception e) {
            result.put("TC", "unavailable");
            result.put("SC", "unavailable");

        }
        return result;
    }
}
