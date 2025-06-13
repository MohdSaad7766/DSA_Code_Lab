package com.CodeLab.Central_Service.service;

import com.CodeLab.Central_Service.exception.AdminEmailAlreadyPresentException;
import com.CodeLab.Central_Service.integration.DBService;
import com.CodeLab.Central_Service.integration.NotificationService;
import com.CodeLab.Central_Service.integration.RabbitMQIntegration;
import com.CodeLab.Central_Service.requestDTO.AdminRequestDTO;
import com.CodeLab.Central_Service.requestDTO.NotificationMessage;
import com.CodeLab.Central_Service.requestDTO.OTPGenerateRequestDTO;
import com.CodeLab.Central_Service.responseDTO.IsEmailAlreadyPresentResponseDTO;
import com.CodeLab.Central_Service.responseDTO.OTPVerificationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AdminService {

    @Autowired
    DBService dbService;

    @Autowired
    RabbitMQIntegration rabbitMQIntegration;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String generateRandomOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // generates a number between 100000 and 999999
        return String.valueOf(otp);
    }

    public void registerAdmin(AdminRequestDTO adminRequestDTO){
        String email = adminRequestDTO.getEmail().trim();
        IsEmailAlreadyPresentResponseDTO responseDTO = dbService.callIsAdminEmailAlreadyExists(email);

        if(responseDTO.isPresent()){
            throw new AdminEmailAlreadyPresentException("The provided Email-"+email+" is already registered!!!");
        }

        OTPGenerateRequestDTO requestDTO = new OTPGenerateRequestDTO();

        String otpNumber = this.generateRandomOTP();

        requestDTO.setEmail(adminRequestDTO.getEmail());
        requestDTO.setOtp(otpNumber);


        //send email to the user is remaining
//        String message = notificationService.callSendMail(requestDTO).getMessage();
//        System.out.println(message);
        NotificationMessage message = new NotificationMessage();
        message.setMessageType("OTP");
        message.setPayload(requestDTO);
        rabbitMQIntegration.insertMessageToQueue(message);

        dbService.callGenerateOtp(requestDTO);



    }

    public OTPVerificationResponseDTO verifyOtp(AdminRequestDTO requestDTO, String otp){
        requestDTO.setEmail(requestDTO.getEmail().trim());;


        OTPVerificationResponseDTO responseDTO = dbService.callVerifyAdminOtp(requestDTO,otp);

        if(responseDTO.isValid()){
            String rawPassword = requestDTO.getPassword().trim();
            String encodedPassword = passwordEncoder.encode(rawPassword);

            requestDTO.setPassword(encodedPassword);
            System.out.println("Raw Pass: "+rawPassword);
            System.out.println("Encoded Pass: "+requestDTO.getPassword());

            dbService.callRegisterAdmin(requestDTO);
        }

        return responseDTO;
    }
}
