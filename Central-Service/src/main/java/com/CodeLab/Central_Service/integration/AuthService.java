package com.CodeLab.Central_Service.integration;

import com.CodeLab.Central_Service.requestDTO.LoginRequestDTO;
import com.CodeLab.Central_Service.responseDTO.TokenValidationResponseDTO;
import com.CodeLab.Central_Service.responseDTO.AdminLoginResponseDTO;
import com.CodeLab.Central_Service.responseDTO.UserLoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

@Component
public class AuthService extends RestAPI{

    @Value("${auth.service.base}")
    String baseURL;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public UserLoginResponseDTO callGenerateUserToken(LoginRequestDTO requestDTO){
        String endpoint = "/token/user/generate";
        HashMap<String,String> map = new HashMap<>();
        map.put("email",requestDTO.getEmail());
        map.put("password",requestDTO.getPassword());


        Object object = this.makeGetCall(baseURL,endpoint,map);
        if (object == null){
            return null;
        }

        return objectMapper.convertValue(object,UserLoginResponseDTO.class);
    }

    public AdminLoginResponseDTO callGenerateAdminToken(LoginRequestDTO requestDTO){
        String endpoint = "/token/admin/generate";
        HashMap<String,String> map = new HashMap<>();
        map.put("email",requestDTO.getEmail());
        map.put("password",requestDTO.getPassword());


        Object object = this.makeGetCall(baseURL,endpoint,map);
        if (object == null){
            return null;
        }

        return objectMapper.convertValue(object,AdminLoginResponseDTO.class);
    }

    public TokenValidationResponseDTO callValidateUserToken(String token) {
        String endpoint = "/token/validate-user";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Build RequestEntity with headers
        RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(baseURL + endpoint))
                .headers(headers)
                .build();

        // Make the request
        ResponseEntity<Object> response = restTemplate.exchange(
                requestEntity,
                Object.class
        );

        return objectMapper.convertValue(response.getBody(), TokenValidationResponseDTO.class);
    }

    public TokenValidationResponseDTO callValidateAdminToken(String token) {
        String endpoint = "/token/validate-admin";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Build RequestEntity with headers
        RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(baseURL + endpoint))
                .headers(headers)
                .build();

        // Make the request
        ResponseEntity<Object> response = restTemplate.exchange(
                requestEntity,
                Object.class
        );

        return objectMapper.convertValue(response.getBody(), TokenValidationResponseDTO.class);
    }

}
