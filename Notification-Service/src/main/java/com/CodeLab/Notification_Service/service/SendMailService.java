package com.CodeLab.Notification_Service.service;

import com.CodeLab.Notification_Service.requestDTO.ContestStartRequestDTO;
import com.CodeLab.Notification_Service.requestDTO.OTPGenerateRequestDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class SendMailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    public  void sendOtpMail(OTPGenerateRequestDTO requestDTO) throws Exception{

        Context context = new Context();
        context.setVariable("otpCode",requestDTO.getOtp());


        String htmlMail = templateEngine.process("RegistrationOTP",context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message,true);

        messageHelper.setSubject("Your CodeLab OTP for Verification");

        messageHelper.setTo(requestDTO.getEmail());
        messageHelper.setText(htmlMail,true);

        javaMailSender.send(message);
    }

    public  void contestReminderMail(ContestStartRequestDTO requestDTO) throws Exception{

        Context context = new Context();
        context.setVariable("name",requestDTO.getUserName());
        context.setVariable("contestName",requestDTO.getContestName());
        context.setVariable("contestStartTime",requestDTO.getContestStartTime());
        context.setVariable("contestEndTime",requestDTO.getContestEndTime());
        context.setVariable("contestDuration",requestDTO.getContestDuration());




        String htmlMail = templateEngine.process("ContestStartReminder",context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message,true);

        messageHelper.setSubject("Your Registered CodeLab Contest Has Started!");

        messageHelper.setTo(requestDTO.getUserEmail());
        messageHelper.setText(htmlMail,true);

        javaMailSender.send(message);
    }

}
