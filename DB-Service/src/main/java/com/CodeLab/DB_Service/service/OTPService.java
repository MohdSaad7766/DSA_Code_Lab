package com.CodeLab.DB_Service.service;

import com.CodeLab.DB_Service.model.OTP;
import com.CodeLab.DB_Service.repository.OTPRepo;
import com.CodeLab.DB_Service.requestDTO.AdminRequestDTO;
import com.CodeLab.DB_Service.requestDTO.OTPGenerateRequestDTO;
import com.CodeLab.DB_Service.requestDTO.UserRequestDTO;
import com.CodeLab.DB_Service.requestdto_converter.OTPConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OTPService {
    @Autowired
    private UserService userService;

    @Autowired
    private OTPRepo otpRepo;

    public void generateOTP(OTPGenerateRequestDTO requestDTO){
        otpRepo.deleteByEmail(requestDTO.getEmail());

        OTP otp = OTPConverter.otpConverter(requestDTO);

        otpRepo.save(otp);
    }

    @Scheduled(fixedRate = 10000) // every 10 Seconds
    public void deleteExpiredOtp() {
        System.out.println("Check for Expired OTP");
        otpRepo.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
    @Scheduled(fixedRate = 10000)  // every 10 Seconds
    public void deleteVerifiedOtp() {
        System.out.println("Check for Verified OTP");
        otpRepo.deleteVerifiedOTPs(true);
    }

    public boolean verifyUserOTP(UserRequestDTO requestDTO, String otpNumber){
        String email = requestDTO.getEmail();


        OTP otp = otpRepo.findByEmailAndOtpAndIsVerifiedFalse(email,otpNumber).orElse(null);

        if(otp == null){
            return false;
        }

        otp.setIsVerified(true);
        otpRepo.save(otp);



        return true;
    }

    public boolean verifyAdminOTP(AdminRequestDTO requestDTO, String otpNumber){
        String email = requestDTO.getEmail();


        OTP otp = otpRepo.findByEmailAndOtpAndIsVerifiedFalse(email,otpNumber).orElse(null);

        if(otp == null){
            return false;
        }

        otp.setIsVerified(true);
        otpRepo.save(otp);



        return true;
    }
}
