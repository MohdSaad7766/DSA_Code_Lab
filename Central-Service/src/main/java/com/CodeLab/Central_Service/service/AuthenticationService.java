package com.CodeLab.Central_Service.service;

import com.CodeLab.Central_Service.integration.AuthService;
import com.CodeLab.Central_Service.requestDTO.LoginRequestDTO;
import com.CodeLab.Central_Service.responseDTO.AdminLoginResponseDTO;
import com.CodeLab.Central_Service.responseDTO.TokenValidationResponseDTO;
import com.CodeLab.Central_Service.responseDTO.UserLoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    public UserLoginResponseDTO generateUserToken(LoginRequestDTO requestDTO){
        requestDTO.setEmail(requestDTO.getEmail().trim());
//        String encodedPass = passwordEncoder.encode();
        requestDTO.setPassword(requestDTO.getPassword().trim());

        return authService.callGenerateUserToken(requestDTO);

    }

    public AdminLoginResponseDTO generateAdminToken(LoginRequestDTO requestDTO){
        requestDTO.setEmail(requestDTO.getEmail().trim());
//        String encodedPass = passwordEncoder.encode();
        requestDTO.setPassword(requestDTO.getPassword().trim());

        return authService.callGenerateAdminToken(requestDTO);

    }

    public TokenValidationResponseDTO validateUserToken(String token){
        return authService.callValidateUserToken(token);
    }

    public TokenValidationResponseDTO validateAdminToken(String token){
        return authService.callValidateAdminToken(token);
    }

}
