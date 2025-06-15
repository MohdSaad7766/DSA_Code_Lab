package com.CodeLab.Auth_Service.controller;

import com.CodeLab.Auth_Service.model.Pair;
import com.CodeLab.Auth_Service.responseDTO.LoginResponseDTO;
import com.CodeLab.Auth_Service.responseDTO.TokenValidationResponseDTO;
import com.CodeLab.Auth_Service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/auth/token")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/generate")
    public ResponseEntity<?> generateToken(@RequestParam String email, @RequestParam String password){
        String jwtToken = authService.generateToken(email,password);

        System.out.println(jwtToken);


        LoginResponseDTO responseDTO = new LoginResponseDTO(jwtToken);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/validate-user")
    public ResponseEntity<?> validateUserToken(@RequestHeader(value = "Authorization", required = false) String header) {
        TokenValidationResponseDTO responseDTO = new TokenValidationResponseDTO();

        if (header == null || !header.startsWith("Bearer ")) {
            responseDTO.setValid(false);
            responseDTO.setMessage("Authorization header is missing or malformed.");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        String token = header.substring(7); // Remove "Bearer " prefix
        Pair pair = authService.validateToken(token,false);

        if (pair == null) {
            responseDTO.setValid(false);
            responseDTO.setMessage("Invalid or expired token.");
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
        responseDTO.setUserId(pair.getUserId());
        responseDTO.setValid(true);
        responseDTO.setMessage("Token is valid.");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);


    }

    @GetMapping("/validate-admin")
    public ResponseEntity<?> validateAdminToken(@RequestHeader(value = "Authorization", required = false) String header) throws UnsupportedEncodingException {

        TokenValidationResponseDTO responseDTO = new TokenValidationResponseDTO();

        if (header == null || !header.startsWith("Bearer ")) {
            responseDTO.setValid(false);
            responseDTO.setMessage("Authorization header is missing or malformed.");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        String token = header.substring(7); // Remove "Bearer " prefix
        Pair pair = authService.validateToken(token,true);

        if (pair == null) {
            responseDTO.setValid(false);
            responseDTO.setMessage("Invalid or expired token.");
            return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
        responseDTO.setUserId(pair.getUserId());
        responseDTO.setValid(true);
        responseDTO.setMessage("Token is valid.");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);


    }


}
