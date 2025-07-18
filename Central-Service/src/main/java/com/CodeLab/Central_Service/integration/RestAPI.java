package com.CodeLab.Central_Service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RestAPI {

    @Autowired
    RestTemplate restTemplate;

    private String addQueryParams(StringBuilder url, HashMap<String, String> params) {
        if (params != null && !params.isEmpty()) {
            url.append("?");
            List<String> queryParts = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue().replace(" ", "%20");
                queryParts.add(key + "=" + value);

            }
            url.append(String.join("&", queryParts));
        }
        return url.toString();
    }


    public Object makePostCall(String baseURL, String endpoint, Object requestBody, HashMap<String, String> queryParams) {
        String url = baseURL + endpoint;
        url = this.addQueryParams(new StringBuilder(url), queryParams);
        URI finalURL = URI.create(url);

        RequestEntity<Object> requestEntity = RequestEntity.post(finalURL).body(requestBody);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalURL, HttpMethod.POST, requestEntity, Object.class);

        return responseEntity.getBody();
    }

    public Object makePutCall(String baseURL, String endpoint, Object requestBody, HashMap<String, String> queryParams) {
        String url = baseURL + endpoint;
        url = this.addQueryParams(new StringBuilder(url), queryParams);

        URI finalURL = URI.create(url);


        RequestEntity<Object> requestEntity = RequestEntity.put(finalURL).body(requestBody);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalURL, HttpMethod.PUT, requestEntity, Object.class);

        return responseEntity.getBody();
    }

    public Object makeDeleteCall(String baseURL, String endpoint, HashMap<String, String> queryParams) {
        String url = baseURL + endpoint;
        url = this.addQueryParams(new StringBuilder(url), queryParams);

        RequestEntity<Void> requestEntity = RequestEntity.delete(URI.create(url)).build();
        ResponseEntity<Object> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Object.class);

        return responseEntity.getBody();
    }

    // ✅ Updated makeGetCall to return List<Object>
    public List<Object> makeGetCallAsList(String baseURL, String endpoint, HashMap<String, String> queryParams) {
        String url = this.addQueryParams(new StringBuilder(baseURL + endpoint), queryParams);

        System.out.println(url);
        RequestEntity<Void> requestEntity = RequestEntity.get(URI.create(url)).build();

        ResponseEntity<List<Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<Object>>() {}
        );

        return responseEntity.getBody();
    }

    // ✅ Optional: Overload if you still want to support single Object response
    public Object makeGetCall(String baseURL, String endpoint, HashMap<String, String> queryParams) {
        String url = this.addQueryParams(new StringBuilder(baseURL + endpoint), queryParams);
//        System.out.println(url);

        RequestEntity<Void> requestEntity = RequestEntity.get(URI.create(url)).build();

        ResponseEntity<Object> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Object.class
        );

        return responseEntity.getBody();
    }
}
