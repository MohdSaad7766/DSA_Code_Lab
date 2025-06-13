package com.CodeLab.Notification_Service.controller;

import com.CodeLab.Notification_Service.requestDTO.OTPGenerateRequestDTO;
import com.CodeLab.Notification_Service.responseDTO.GeneralResponseDTO;
import com.CodeLab.Notification_Service.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification/email")
public class SendEmailController {

    @Autowired
    SendMailService sendMailService;

    @PostMapping("/registration-otp")
    public ResponseEntity<?> sendOtpEmail(@RequestBody OTPGenerateRequestDTO requestDTO){
        GeneralResponseDTO responseDTO = new GeneralResponseDTO();
        try {
            sendMailService.sendOtpMail(requestDTO);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
