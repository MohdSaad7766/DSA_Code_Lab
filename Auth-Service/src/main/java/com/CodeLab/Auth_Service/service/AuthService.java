package com.CodeLab.Auth_Service.service;

import com.CodeLab.Auth_Service.integration.DBService;
import com.CodeLab.Auth_Service.model.Admin;
import com.CodeLab.Auth_Service.model.Pair;
import com.CodeLab.Auth_Service.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;

@Slf4j
@Service
public class AuthService {
    @Autowired
    DBService dbService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${auth.secret.key}")
    String secretKey;

    Long expirationTime = 604800000L;  //7 days

    public String generateToken(String email, String password) {


        String credentials = email + ":" + password;

        String jwtToken = Jwts.builder().setSubject(credentials)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return jwtToken;
    }

    public String decryptToken(String token) {
        try {
            String credentials = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            log.info("Token decrypted successfully: {}", credentials);
            return credentials;
        } catch (Exception e) {
            log.error("Token decryption failed: {}", e.getMessage());
            return null; // Or throw a custom exception
        }
    }

    public Pair validateToken(String token, boolean isAdmin) {
        try {
            String credentials = decryptToken(token);
            if (credentials == null) {
                return null;
            }

            String[] parts = credentials.split(":");
            if (parts.length != 2) {
                return null;
            }

            String email = parts[0];
            String password = parts[1];

            if (isAdmin) {
                Admin response = dbService.callGetAdminByEmail(email);
                if (response == null) {
                    return null;
                }
                boolean isPasswordValid = passwordEncoder.matches(password.trim(), response.getPassword());
                return isPasswordValid ? new Pair(credentials, response, response.getAdminId()) : null;
            } else {
                User response = dbService.callGetUserByEmail(email);
                if (response == null) {
                    log.warn("User not found for email: {}", email);
                    return null;
                }
                boolean isPasswordValid = passwordEncoder.matches(password.trim(), response.getPassword());
                return isPasswordValid ? new Pair(credentials, response, response.getUserId()) : null;
            }


        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return null;
        }
    }
}

